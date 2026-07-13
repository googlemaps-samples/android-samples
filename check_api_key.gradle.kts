import java.util.Properties
import org.gradle.api.GradleException

/**
 * Standalone API Key & Secrets Enforcement Script
 *
 * Checks for a valid Google Maps API key (starts with 'AIza') before executing build tasks.
 * Designed to be imported on-demand into application modules.
 */

fun resolveSecretsFile(): File {
    val localFile = rootProject.file("secrets.properties")
    if (localFile.exists()) return localFile

    // 2. Parent directory (for worktrees)
    val parentFile = File(rootProject.projectDir.parentFile, "secrets.properties")
    if (parentFile.exists()) {
        println("Found master secrets.properties in parent directory (${parentFile.absolutePath}). Linking locally.")
        try {
            java.nio.file.Files.createSymbolicLink(localFile.toPath(), parentFile.toPath())
        } catch (e: Exception) {
            localFile.writeBytes(parentFile.readBytes())
        }
        return localFile
    }

    // 3. User Home Directory (~/.android/secrets.properties)
    val homeFile = File(File(System.getProperty("user.home"), ".android"), "secrets.properties")
    if (homeFile.exists()) {
        println("Found master secrets.properties in ~/.android/secrets.properties. Linking locally.")
        try {
            java.nio.file.Files.createSymbolicLink(localFile.toPath(), homeFile.toPath())
        } catch (e: Exception) {
            localFile.writeBytes(homeFile.readBytes())
        }
        return localFile
    }

    return localFile // Fallback to local to trigger creation prompt
}

val secretsFile = resolveSecretsFile()
println("Resolved secrets.properties path: ${secretsFile.absolutePath}")
val isCI = System.getenv("CI")?.toBoolean() ?: false

if (!isCI) {
    val requestedTasks = gradle.startParameter.taskNames

    // 1. Allow Android Studio IDE sync (empty task invocation) to complete successfully
    if (requestedTasks.isEmpty() && !secretsFile.exists()) {
        println("⚠️ Warning: secrets.properties not found. Gradle sync will succeed, but building/running the app will fail.")
    } else if (requestedTasks.isNotEmpty()) {

        // 2. Identify if the current invocation builds or installs the application
        val buildTaskKeywords = listOf("build", "install", "assemble", "bundle")
        val isBuildTask = requestedTasks.any { task ->
            buildTaskKeywords.any { keyword -> task.contains(keyword, ignoreCase = true) }
        }

        // 3. Allow pure verification runs (unit tests, static analysis) to proceed without keys
        val testTaskKeywords = listOf("test", "report", "lint")
        val isTestTask = requestedTasks.any { task ->
            testTaskKeywords.any { keyword -> task.contains(keyword, ignoreCase = true) }
        }

        if (isBuildTask && !isTestTask) {
            val defaultsFile = rootProject.file("local.defaults.properties")
            val requiredKeysMessage = if (defaultsFile.exists()) {
                defaultsFile.readText()
            } else {
                "MAPS_API_KEY=<YOUR_API_KEY>"
            }

            if (!secretsFile.exists()) {
                throw GradleException(
                    "Build Blocked: 'secrets.properties' file not found.\n" +
                    "Please create 'secrets.properties' in the root project directory with the following content:\n\n" +
                    requiredKeysMessage
                )
            }

            // 4. Validate key integrity via Properties and Regex checking
            val secrets = Properties()
            secretsFile.inputStream().use { secrets.load(it) }

            // Check for relevant key names (e.g., MAPS_API_KEY or MAPS3D_API_KEY)
            val apiKey = secrets.getProperty("MAPS_API_KEY") ?: secrets.getProperty("MAPS3D_API_KEY") ?: ""
            println("Checking API Key in secrets.properties: '$apiKey'")

            if (apiKey.isBlank() || !apiKey.matches(Regex("^AIza[a-zA-Z0-9_-]{35}$"))) {
                throw GradleException(
                    "Build Blocked: Invalid or missing Google Maps API key in 'secrets.properties'.\n" +
                    "Please provide a valid API key starting with 'AIza'."
                )
            }
        }
    }
}

/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.common_ui.catalog.compose

import com.example.common_ui.catalog.Framework

/**
 * Single Source of Truth Code Provider.
 *
 * STRICT RULE: Only quotes code surrounded with official region tags
 * (// [START <tag>] ... // [END <tag>]).
 *
 * This guarantees complete consistency between the source code, samples,
 * in-app catalog reviewer, and Google Maps Platform documentation.
 * If a sample does not have official region tags, no snippet is quoted.
 */
object SampleCodeProvider {

    data class SnippetPair(
        val regionTag: String,
        val kotlinCode: String,
        val javaCode: String
    )

    fun hasCode(sampleId: String): Boolean {
        return findSnippet(sampleId) != null
    }

    fun getRegionTag(sampleId: String): String? {
        return findSnippet(sampleId)?.regionTag
    }

    fun getCode(sampleId: String, framework: Framework): String {
        val snippet = findSnippet(sampleId) ?: return ""
        return when (framework) {
            Framework.KOTLIN_VIEWS -> snippet.kotlinCode
            Framework.JAVA_VIEWS -> snippet.javaCode
        }
    }

    private fun findSnippet(sampleId: String): SnippetPair? {
        return SNIPPETS[sampleId]
            ?: SNIPPETS.entries.firstOrNull { sampleId.endsWith(it.key.substringAfterLast('.')) }?.value
    }

    private val SNIPPETS = mapOf(
        "com.example.kotlindemos.BasicMapDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_basic_map",
            kotlinCode = """
@Sample(
    id = "basic_map",
    title = "Basic Map",
    description = "Fundamental map instantiation, lifecycle binding, and default camera centering.",
    category = "Map Initialization",
    complexity = Complexity.SNIPPET,
    tags = ["#map", "#init", "#lifecycle", "#quickstart"],
    purpose = "Demonstrates clean, minimal map instantiation using SupportMapFragment.",
    successCriteria = "The map loads default vector tiles cleanly centered at the initial coordinates with working gestures.",
    failureIndicators = "Grey tiles (missing API key or auth mismatch), crash on back navigation, or map failing to unpause.",
    framework = Framework.KOTLIN_VIEWS
)
class BasicMapDemoActivity : SamplesBaseActivity(), OnMapReadyCallback {

    val SYDNEY = LatLng(-33.862, 151.21)
    val ZOOM_LEVEL = 13f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.basic_demo)
        val mapFragment : SupportMapFragment? =
                supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just move the camera to Sydney and add a marker in Sydney.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(SYDNEY))
        }
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "basic_map",
    title = "Basic Map",
    description = "Fundamental map instantiation, lifecycle binding, and default camera centering.",
    category = "Map Initialization",
    complexity = Complexity.SNIPPET,
    tags = {"#map", "#init", "#lifecycle", "#quickstart"},
    purpose = "Demonstrates clean, minimal map instantiation using SupportMapFragment.",
    successCriteria = "The map loads default vector tiles cleanly centered at the initial coordinates with working gestures.",
    failureIndicators = "Grey tiles (missing API key or auth mismatch), crash on back navigation, or map failing to unpause.",
    framework = Framework.JAVA_VIEWS
)
public class BasicMapDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
""".trimIndent()
        ),

        "com.example.kotlindemos.UiSettingsDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_ui_settings",
            kotlinCode = """
override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        uiSettings = map.uiSettings

        // Keep the UI Settings state in sync with the checkboxes.
        uiSettings.isZoomControlsEnabled = binding.zoomButtonsToggle.isChecked
        uiSettings.isCompassEnabled = binding.compassToggle.isChecked
        uiSettings.isMyLocationButtonEnabled = binding.mylocationbuttonToggle.isChecked
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = binding.mylocationlayerToggle.isChecked
        uiSettings.isScrollGesturesEnabled = binding.scrollToggle.isChecked
        uiSettings.isZoomGesturesEnabled = binding.zoomGesturesToggle.isChecked
        uiSettings.isTiltGesturesEnabled = binding.tiltToggle.isChecked
        uiSettings.isRotateGesturesEnabled = binding.rotateToggle.isChecked
    }
""".trimIndent(),
            javaCode = """
@SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mUiSettings = mMap.getUiSettings();

        // Keep the UI Settings state in sync with the checkboxes.
        mUiSettings.setZoomControlsEnabled(binding.zoomButtonsToggle.isChecked());
        mUiSettings.setCompassEnabled(binding.compassToggle.isChecked());
        mUiSettings.setMyLocationButtonEnabled(binding.mylocationbuttonToggle.isChecked());
        mUiSettings.setScrollGesturesEnabled(binding.scrollToggle.isChecked());
        mUiSettings.setZoomGesturesEnabled(binding.zoomGesturesToggle.isChecked());
        mUiSettings.setTiltGesturesEnabled(binding.tiltToggle.isChecked());
        mUiSettings.setRotateGesturesEnabled(binding.rotateToggle.isChecked());

        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(binding.mylocationlayerToggle.isChecked());
    }
""".trimIndent()
        ),

        "com.example.kotlindemos.PolylineDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_polylines",
            kotlinCode = """
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.polyline_demo)

        hueBar = findViewById<SeekBar>(com.example.common_ui.R.id.hueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = 0
        }

        alphaBar = findViewById<SeekBar>(com.example.common_ui.R.id.alphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        widthBar = findViewById<SeekBar>(com.example.common_ui.R.id.widthSeekBar).apply {
            max = MAX_WIDTH_PX
            progress = MAX_WIDTH_PX / 2
        }

        startCapSpinner = findViewById<Spinner>(com.example.common_ui.R.id.startCapSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(capTypeNameResourceIds))
        }

        endCapSpinner = findViewById<Spinner>(com.example.common_ui.R.id.endCapSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(capTypeNameResourceIds))
        }

        jointTypeSpinner = findViewById<Spinner>(com.example.common_ui.R.id.jointTypeSpinner).apply {
            adapter = ArrayAdapter(this@PolylineDemoActivity,
                    android.R.layout.simple_spinner_item,
                    getResourceStrings(jointTypeNameResourceIds))
        }

        patternSpinner = findViewById<Spinner>(com.example.common_ui.R.id.patternSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolylineDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(patternTypeNameResourceIds))
        }

        clickabilityCheckbox = findViewById<CheckBox>(com.example.common_ui.R.id.toggleClickability)

        val mapFragment = supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById(com.example.common_ui.R.id.map_container))
    }
    

    override fun onMapReady(googleMap: GoogleMap) {
        with(googleMap) {
            // Override the default content description on the view, for accessibility mode.
            setContentDescription(getString(com.example.common_ui.R.string.polyline_demo_description))

            // A geodesic polyline that goes around the world.
            addPolyline(PolylineOptions().apply {
                add(lhrLatLng, aklLatLng, laxLatLng, jfkLatLng, lhrLatLng)
                width(INITIAL_STROKE_WIDTH_PX.toFloat())
                color(Color.BLUE)
                geodesic(true)
                clickable(clickabilityCheckbox.isChecked)
            })

            // Move the googleMap so that it is centered on the mutable polyline.
            moveCamera(CameraUpdateFactory.newLatLngZoom(melbourneLatLng, 3f))

            // Add a listener for polyline clicks that changes the clicked polyline's color.
            setOnPolylineClickListener { polyline ->
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.color = polyline.color xor 0x00ffffff
            }
        }

        // A simple polyline across Australia. This polyline will be mutable.
        mutablePolyline = googleMap.addPolyline(PolylineOptions().apply{
            color(Color.HSVToColor(
                    alphaBar.progress, floatArrayOf(hueBar.progress.toFloat(), 1f, 1f)))
            width(widthBar.progress.toFloat())
            clickable(clickabilityCheckbox.isChecked)
            add(melbourneLatLng, adelaideLatLng, perthLatLng, darwinLatLng)
        })

        arrayOf(hueBar, alphaBar, widthBar).map {
            it.setOnSeekBarChangeListener(this)
        }

        arrayOf(startCapSpinner, endCapSpinner, jointTypeSpinner, patternSpinner).map {
            it.onItemSelectedListener = this
        }

        with(mutablePolyline) {
            startCap = getSelectedCap(startCapSpinner.selectedItemPosition) ?: ButtCap()
            endCap = getSelectedCap(endCapSpinner.selectedItemPosition) ?: ButtCap()
            jointType = getSelectedJointType(jointTypeSpinner.selectedItemPosition)
            pattern = getSelectedPattern(patternSpinner.selectedItemPosition)
        }

        clickabilityCheckbox.setOnClickListener {
            view -> mutablePolyline.isClickable = (view as CheckBox).isChecked
        }
    }
""".trimIndent(),
            javaCode = """
@Sample(
    id = "polylines",
    title = "Polylines & Patterns",
    description = "Drawing polylines with joint types, dash/dot stroke patterns, joint styles, and spans.",
    category = "Shapes & Geometry",
    complexity = Complexity.SIMPLE,
    tags = {"#shapes", "#polylines", "#patterns", "#dashes", "#stroke", "#routes"},
    purpose = "Demonstrates drawing customizable polylines with dash/gap patterns, round end caps, and bevel joints.",
    successCriteria = "Polylines render crisp dashed and dotted stroke lines along coordinate vertices.",
    failureIndicators = "Line caps distorted or custom pattern ignored on high-DPI screens.",
    framework = Framework.JAVA_VIEWS
)
public class PolylineDemoActivity extends SamplesBaseActivity
        implements OnSeekBarChangeListener, OnItemSelectedListener, OnMapReadyCallback {

    // City locations for mutable polyline.
    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
    private static final LatLng DARWIN = new LatLng(-12.4258647, 130.7932231);
    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
    private static final LatLng PERTH = new LatLng(-31.95285, 115.85734);

    // Airport locations for geodesic polyline.
    private static final LatLng AKL = new LatLng(-37.006254, 174.783018);
    private static final LatLng JFK = new LatLng(40.641051, -73.777485);
    private static final LatLng LAX = new LatLng(33.936524, -118.377686);
    private static final LatLng LHR = new LatLng(51.471547, -0.460052);

    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;
    private static final int CUSTOM_CAP_IMAGE_REF_WIDTH_PX = 50;
    private static final int INITIAL_STROKE_WIDTH_PX = 5;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private Polyline mutablePolyline;
    private SeekBar hueBar;
    private SeekBar alphaBar;
    private SeekBar widthBar;
    private Spinner startCapSpinner;
    private Spinner endCapSpinner;
    private Spinner jointTypeSpinner;
    private Spinner patternSpinner;
    private CheckBox clickabilityCheckbox;

    // These are the options for polyline caps, joints and patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] CAP_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.cap_butt, // Default
            com.example.common_ui.R.string.cap_round,
            com.example.common_ui.R.string.cap_square,
            com.example.common_ui.R.string.cap_image,
    };

    private static final int[] JOINT_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.joint_type_default, // Default
            com.example.common_ui.R.string.joint_type_bevel,
            com.example.common_ui.R.string.joint_type_round,
    };

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.pattern_solid, // Default
            com.example.common_ui.R.string.pattern_dashed,
            com.example.common_ui.R.string.pattern_dotted,
            com.example.common_ui.R.string.pattern_mixed,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.polyline_demo);

        hueBar = findViewById(com.example.common_ui.R.id.hueSeekBar);
        hueBar.setMax(MAX_HUE_DEGREES);
        hueBar.setProgress(0);

        alphaBar = findViewById(com.example.common_ui.R.id.alphaSeekBar);
        alphaBar.setMax(MAX_ALPHA);
        alphaBar.setProgress(MAX_ALPHA);

        widthBar = findViewById(com.example.common_ui.R.id.widthSeekBar);
        widthBar.setMax(MAX_WIDTH_PX);
        widthBar.setProgress(MAX_WIDTH_PX / 2);

        startCapSpinner = findViewById(com.example.common_ui.R.id.startCapSpinner);
        startCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        endCapSpinner = findViewById(com.example.common_ui.R.id.endCapSpinner);
        endCapSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(CAP_TYPE_NAME_RESOURCE_IDS)));

        jointTypeSpinner = findViewById(com.example.common_ui.R.id.jointTypeSpinner);
        jointTypeSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(JOINT_TYPE_NAME_RESOURCE_IDS)));

        patternSpinner = findViewById(com.example.common_ui.R.id.patternSpinner);
        patternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        clickabilityCheckbox = findViewById(com.example.common_ui.R.id.toggleClickability);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }
    

    @Override
    public void onMapReady(GoogleMap map) {

        // Override the default content description on the view, for accessibility mode.
        map.setContentDescription(getString(com.example.common_ui.R.string.polyline_demo_description));

        // A geodesic polyline that goes around the world.
        map.addPolyline(new PolylineOptions()
                .add(LHR, AKL, LAX, JFK, LHR)
                .width(INITIAL_STROKE_WIDTH_PX)
                .color(Color.BLUE)
                .geodesic(true)
                .clickable(clickabilityCheckbox.isChecked()));

        // A simple polyline across Australia. This polyline will be mutable.
        int color = Color.HSVToColor(
                alphaBar.getProgress(), new float[]{hueBar.getProgress(), 1, 1});
        mutablePolyline = map.addPolyline(new PolylineOptions()
                .color(color)
                .width(widthBar.getProgress())
                .clickable(clickabilityCheckbox.isChecked())
                .add(MELBOURNE, ADELAIDE, PERTH, DARWIN));

        hueBar.setOnSeekBarChangeListener(this);
        alphaBar.setOnSeekBarChangeListener(this);
        widthBar.setOnSeekBarChangeListener(this);

        startCapSpinner.setOnItemSelectedListener(this);
        endCapSpinner.setOnItemSelectedListener(this);
        jointTypeSpinner.setOnItemSelectedListener(this);
        patternSpinner.setOnItemSelectedListener(this);

        mutablePolyline.setStartCap(getSelectedCap(startCapSpinner.getSelectedItemPosition()));
        mutablePolyline.setEndCap(getSelectedCap(endCapSpinner.getSelectedItemPosition()));
        mutablePolyline.setJointType(getSelectedJointType(jointTypeSpinner.getSelectedItemPosition()));
        mutablePolyline.setPattern(getSelectedPattern(patternSpinner.getSelectedItemPosition()));

        // Move the map so that it is centered on the mutable polyline.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MELBOURNE, 3));

        // Add a listener for polyline clicks that changes the clicked polyline's color.
        map.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.setColor(polyline.getColor() ^ 0x00ffffff);
            }
        });
    }

    
}
""".trimIndent()
        ),

        "com.example.kotlindemos.PolygonDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_polygons",
            kotlinCode = """
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.polygon_demo)

        fillHueBar = findViewById<SeekBar>(R.id.fillHueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = MAX_HUE_DEGREES / 2
        }

        fillAlphaBar = findViewById<SeekBar>(R.id.fillAlphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA / 2
        }

        strokeWidthBar = findViewById<SeekBar>(R.id.strokeWidthSeekBar).apply {
            max = MAX_WIDTH_PX
            progress = MAX_WIDTH_PX / 3
        }

        strokeHueBar = findViewById<SeekBar>(R.id.strokeHueSeekBar).apply {
            max = MAX_HUE_DEGREES
            progress = 0
        }

        strokeAlphaBar = findViewById<SeekBar>(R.id.strokeAlphaSeekBar).apply {
            max = MAX_ALPHA
            progress = MAX_ALPHA
        }

        strokeJointTypeSpinner = findViewById<Spinner>(R.id.strokeJointTypeSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolygonDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(jointTypeNameResourceIds))
        }

        strokePatternSpinner = findViewById<Spinner>(R.id.strokePatternSpinner).apply {
            adapter = ArrayAdapter(
                    this@PolygonDemoActivity, android.R.layout.simple_spinner_item,
                    getResourceStrings(patternTypeNameResourceIds))
        }

        clickabilityCheckbox = findViewById(R.id.toggleClickability)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(findViewById(R.id.map_container))
    }
    

    override fun onMapReady(googleMap: GoogleMap) {
        val fillColorArgb = Color.HSVToColor(
                fillAlphaBar.progress, floatArrayOf(fillHueBar.progress.toFloat(), 1f, 1f))
        val strokeColorArgb = Color.HSVToColor(
                strokeAlphaBar.progress, floatArrayOf(strokeHueBar.progress.toFloat(), 1f, 1f))

        with(googleMap) {
            // Override the default content description on the view, for accessibility mode.
            setContentDescription(getString(R.string.polygon_demo_description))
            // Move the googleMap so that it is centered on the mutable polygon.
            moveCamera(CameraUpdateFactory.newLatLngZoom(center, 4f))

            // Create a rectangle with two rectangular holes.
            mutablePolygon = addPolygon(PolygonOptions().apply {
                addAll(createRectangle(center, 5.0, 5.0))
                addHole(createRectangle(LatLng(-22.0, 128.0), 1.0, 1.0))
                addHole(createRectangle(LatLng(-18.0, 133.0), 0.5, 1.5))
                fillColor(fillColorArgb)
                strokeColor(strokeColorArgb)
                strokeWidth(strokeWidthBar.progress.toFloat())
                clickable(clickabilityCheckbox.isChecked)
            })

            // Add a listener for polygon clicks that changes the clicked polygon's stroke color.
            setOnPolygonClickListener { polygon ->
                // Flip the red, green and blue components of the polygon's stroke color.
                polygon.strokeColor = polygon.strokeColor xor 0x00ffffff
            }
        }

        // set listeners on seekBars
        arrayOf(fillHueBar, fillAlphaBar, strokeWidthBar, strokeHueBar, strokeAlphaBar).map {
            it.setOnSeekBarChangeListener(this)
        }

        // set listeners on spinners
        arrayOf(strokeJointTypeSpinner, strokePatternSpinner).map {
            it.onItemSelectedListener = this
        }

        // set line pattern and joint type based on current spinner position
        with(mutablePolygon) {
            strokeJointType = getSelectedJointType(strokeJointTypeSpinner.selectedItemPosition)
            strokePattern = getSelectedPattern(strokePatternSpinner.selectedItemPosition)
        }

    }
""".trimIndent(),
            javaCode = """
@Sample(
    id = "polygons",
    title = "Polygons & Holes",
    description = "Drawing geodesic polygons with fill colors, stroke patterns, click events, and interior holes.",
    category = "Shapes & Geometry",
    complexity = Complexity.SIMPLE,
    tags = {"#shapes", "#polygons", "#holes", "#geometry", "#stroke", "#fill"},
    purpose = "Demonstrates drawing styled polygons with interior holes (donut polygons), click listeners, and stroke caps.",
    successCriteria = "Polygons render with specified fill opacity and interior cutout holes properly subtracted.",
    failureIndicators = "Holes not rendering as transparent cutouts or stroke color incorrect.",
    framework = Framework.JAVA_VIEWS
)
public class PolygonDemoActivity extends SamplesBaseActivity
        implements OnSeekBarChangeListener, OnItemSelectedListener, OnMapReadyCallback {

    private static final LatLng CENTER = new LatLng(-20, 130);
    private static final int MAX_WIDTH_PX = 100;
    private static final int MAX_HUE_DEGREES = 360;
    private static final int MAX_ALPHA = 255;

    private static final int PATTERN_DASH_LENGTH_PX = 50;
    private static final int PATTERN_GAP_LENGTH_PX = 10;
    private static final Dot DOT = new Dot();
    private static final Dash DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);
    private static final List<PatternItem> PATTERN_DASHED = Arrays.asList(DASH, GAP);
    private static final List<PatternItem> PATTERN_MIXED = Arrays.asList(DOT, GAP, DOT, DASH, GAP);

    private Polygon mutablePolygon;
    private SeekBar fillHueBar;
    private SeekBar fillAlphaBar;
    private SeekBar strokeWidthBar;
    private SeekBar strokeHueBar;
    private SeekBar strokeAlphaBar;
    private Spinner strokeJointTypeSpinner;
    private Spinner strokePatternSpinner;
    private CheckBox clickabilityCheckbox;

    // These are the options for polygon stroke joints and patterns. We use their
    // string resource IDs as identifiers.

    private static final int[] JOINT_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.joint_type_default, // Default
            com.example.common_ui.R.string.joint_type_bevel,
            com.example.common_ui.R.string.joint_type_round,
    };

    private static final int[] PATTERN_TYPE_NAME_RESOURCE_IDS = {
            com.example.common_ui.R.string.pattern_solid, // Default
            com.example.common_ui.R.string.pattern_dashed,
            com.example.common_ui.R.string.pattern_dotted,
            com.example.common_ui.R.string.pattern_mixed,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.polygon_demo);

        fillHueBar = findViewById(com.example.common_ui.R.id.fillHueSeekBar);
        fillHueBar.setMax(MAX_HUE_DEGREES);
        fillHueBar.setProgress(MAX_HUE_DEGREES / 2);

        fillAlphaBar = findViewById(com.example.common_ui.R.id.fillAlphaSeekBar);
        fillAlphaBar.setMax(MAX_ALPHA);
        fillAlphaBar.setProgress(MAX_ALPHA / 2);

        strokeWidthBar = findViewById(com.example.common_ui.R.id.strokeWidthSeekBar);
        strokeWidthBar.setMax(MAX_WIDTH_PX);
        strokeWidthBar.setProgress(MAX_WIDTH_PX / 3);

        strokeHueBar = findViewById(com.example.common_ui.R.id.strokeHueSeekBar);
        strokeHueBar.setMax(MAX_HUE_DEGREES);
        strokeHueBar.setProgress(0);

        strokeAlphaBar = findViewById(com.example.common_ui.R.id.strokeAlphaSeekBar);
        strokeAlphaBar.setMax(MAX_ALPHA);
        strokeAlphaBar.setProgress(MAX_ALPHA);

        strokeJointTypeSpinner = findViewById(com.example.common_ui.R.id.strokeJointTypeSpinner);
        strokeJointTypeSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(JOINT_TYPE_NAME_RESOURCE_IDS)));

        strokePatternSpinner = findViewById(com.example.common_ui.R.id.strokePatternSpinner);
        strokePatternSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                getResourceStrings(PATTERN_TYPE_NAME_RESOURCE_IDS)));

        clickabilityCheckbox = findViewById(com.example.common_ui.R.id.toggleClickability);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }
    

    @Override
    public void onMapReady(GoogleMap map) {
        // Override the default content description on the view, for accessibility mode.
        map.setContentDescription(getString(com.example.common_ui.R.string.polygon_demo_description));

        int fillColorArgb = Color.HSVToColor(
                fillAlphaBar.getProgress(), new float[]{fillHueBar.getProgress(), 1, 1});
        int strokeColorArgb = Color.HSVToColor(
                strokeAlphaBar.getProgress(), new float[]{strokeHueBar.getProgress(), 1, 1});

        // Create a rectangle with two rectangular holes.
        mutablePolygon = map.addPolygon(new PolygonOptions()
                .addAll(createRectangle(CENTER, 5, 5))
                .addHole(createRectangle(new LatLng(-22, 128), 1, 1))
                .addHole(createRectangle(new LatLng(-18, 133), 0.5, 1.5))
                .fillColor(fillColorArgb)
                .strokeColor(strokeColorArgb)
                .strokeWidth(strokeWidthBar.getProgress())
                .clickable(clickabilityCheckbox.isChecked()));

        fillHueBar.setOnSeekBarChangeListener(this);
        fillAlphaBar.setOnSeekBarChangeListener(this);

        strokeWidthBar.setOnSeekBarChangeListener(this);
        strokeHueBar.setOnSeekBarChangeListener(this);
        strokeAlphaBar.setOnSeekBarChangeListener(this);

        strokeJointTypeSpinner.setOnItemSelectedListener(this);
        strokePatternSpinner.setOnItemSelectedListener(this);

        mutablePolygon.setStrokeJointType(getSelectedJointType(strokeJointTypeSpinner.getSelectedItemPosition()));
        mutablePolygon.setStrokePattern(getSelectedPattern(strokePatternSpinner.getSelectedItemPosition()));

        // Move the map so that it is centered on the mutable polygon.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(CENTER, 4));

        // Add a listener for polygon clicks that changes the clicked polygon's stroke color.
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                // Flip the red, green and blue components of the polygon's stroke color.
                polygon.setStrokeColor(polygon.getStrokeColor() ^ 0x00ffffff);
            }
        });
    }

    
}
""".trimIndent()
        ),

        "com.example.kotlindemos.AdvancedMarkersDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_marker_advanced",
            kotlinCode = """
@Sample(
    id = "advanced_markers",
    title = "Advanced Markers & Pins",
    description = "Modern PinConfig pins, custom glyphs, badge icon views, and collision behavior.",
    category = "Markers & Overlays",
    complexity = Complexity.ADVANCED,
    tags = ["#markers", "#advancedmarkers", "#pinconfig", "#collision", "#badges", "#mapid"],
    purpose = "Demonstrates Cloud-backed Advanced Markers with custom colors, pin glyphs, collision behaviors, and custom View icons.",
    successCriteria = "Custom colored pins and badge icon views render sharply at correct anchor points with collision handling.",
    failureIndicators = "Pins render as default red markers (missing Map ID), collision behavior ignored, or badge text blurry.",
    framework = Framework.KOTLIN_VIEWS
)
class AdvancedMarkersDemoActivity : SamplesBaseActivity(), OnMapReadyCallback {

    /**
     * This method is called when the activity is first created.
     *
     * It sets up the activity's layout and then initializes the map.
     *
     * The key logic here is to check if the developer has provided a Map ID in the
     * `strings.xml` file.
     *
     * If the `R.string.map_id` value is not the default "DEMO_MAP_ID", it means a
     * custom Map ID has been provided. In this case, we can rely on the simpler setup
     * where the `SupportMapFragment` is inflated directly from the XML layout, and it
     * will automatically use the Map ID from the string resource.
     *
     * However, if the `R.string.map_id` is still the default value, we fall back to a
     * programmatic setup. This involves:
     * 1. Retrieving the Map ID from the `secrets.properties` file, which is managed by the
     *    `ApiDemoApplication` class.
     * 2. Creating a `GoogleMapOptions` object.
     * 3. Explicitly setting the retrieved `mapId` on the `GoogleMapOptions`. This step is
     *    **critical** because Advanced Markers will not work without a valid Map ID.
     * 4. Creating a new `SupportMapFragment` instance with these options and replacing the
     *    placeholder fragment in the layout.
     *
     * This dual approach ensures that the demo can run seamlessly while also providing a
     * clear path for developers to use their own Map IDs, which is a requirement for using
     * Advanced Markers.
     */
    /**
     * This method is called when the activity is first created.
     *
     * It sets up the activity's layout and then initializes the map.
     *
     * The key logic here is to check if the developer has provided a Map ID in the
     * `strings.xml` file.
     *
     * If the `R.string.map_id` value is not the default "DEMO_MAP_ID", it means a
     * custom Map ID has been provided. In this case, we can rely on the simpler setup
     * where the `SupportMapFragment` is inflated directly from the XML layout, and it
     * will automatically use the Map ID from the string resource.
     *
     * However, if the `R.string.map_id` is still the default value, we fall back to a
     * programmatic setup. This involves:
     * 1. Retrieving the Map ID from the `secrets.properties` file, via the
     *    `ApiDemoApplication.mapId` property.
     * 2. Creating a `GoogleMapOptions` object.
     * 3. Explicitly setting the retrieved `mapId` on the `GoogleMapOptions`. This step is
     *    **critical** because Advanced Markers will not work without a valid Map ID.
     * 4. Creating a new `SupportMapFragment` instance with these options and replacing the
     *    placeholder fragment in the layout.
     *
     * This dual approach ensures that the demo can run seamlessly while also providing a
     * clear path for developers to use their own Map IDs, which is a requirement for using
     * Advanced Markers.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.common_ui.R.layout.advanced_markers_demo)

        if (getString(com.example.common_ui.R.string.map_id) != "DEMO_MAP_ID") {
            val mapFragment = supportFragmentManager.findFragmentById(com.example.common_ui.R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        } else {
            val mapId = (application as ApiDemoApplication).mapId

            // --- Map ID Check ---
            if (mapId == null) {
                finish()
                return // Exit early if no valid Map ID
            }

            // --- Programmatically create and add the map fragment ---
            val mapOptions = GoogleMapOptions().apply {
                mapId(mapId)
            }
            val mapFragment = SupportMapFragment.newInstance(mapOptions)
            supportFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment) // Use the container ID
                .commit()
            mapFragment.getMapAsync(this)
        }

        applyInsets(findViewById(com.example.common_ui.R.id.map_container))
    }

    override fun onMapReady(map: GoogleMap) {

        val bounds = LatLngBounds.builder()
            .include(SINGAPORE)
            .include(KUALA_LUMPUR)
            .include(JAKARTA)
            .include(BANGKOK)
            .include(MANILA)
            .include(HO_CHI_MINH_CITY)
            .build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))

        val capabilities: MapCapabilities = map.mapCapabilities
        Log.d(TAG, "are advanced marker enabled?" + capabilities.isAdvancedMarkersAvailable)

        // 1. Custom View as iconView (Framed circular badge with Android logo)
        val iconImageView = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.ic_android)
            setColorFilter("#3DDC84".toColorInt()) // Android Green
            setBackgroundResource(R.drawable.bg_marker_badge)
            val padding = (8 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
            layoutParams = android.view.ViewGroup.LayoutParams(
                (44 * resources.displayMetrics.density).toInt(),
                (44 * resources.displayMetrics.density).toInt()
            )
        }
        map.addMarker(
            AdvancedMarkerOptions()
                .position(SINGAPORE)
                .iconView(iconImageView)
                .title("Singapore (Custom Framed Badge)")
                .zIndex(1f)
        )

        // 2. PinConfig with custom background color
        val pinConfigMagenta = PinConfig.builder()
            .setBackgroundColor(Color.MAGENTA)
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigMagenta))
                .position(KUALA_LUMPUR)
                .title("Kuala Lumpur (Magenta Pin)")
        )

        // 3. PinConfig with custom border color
        val pinConfigBorder = PinConfig.builder()
            .setBorderColor(Color.BLUE)
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigBorder))
                .position(JAKARTA)
                .title("Jakarta (Blue Border)")
        )

        // 4. PinConfig with text glyph ("A")
        val pinConfigTextGlyph = PinConfig.builder()
            .setGlyph(PinConfig.Glyph("A"))
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigTextGlyph))
                .position(BANGKOK)
                .title("Bangkok (Text Glyph 'A')")
        )

        // 5. PinConfig with transparent glyph (cutout / donut pin)
        val pinConfigHole = PinConfig.builder()
            .setBackgroundColor(Color.MAGENTA)
            .setGlyph(PinConfig.Glyph(Color.TRANSPARENT))
            .build()
        map.addMarker(
            AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfigHole))
                .position(MANILA)
                .title("Manila (Transparent Cutout Glyph)")
        )

        // 6. Collision behavior
        val collisionBehavior =
            AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
        map.addMarker(
            AdvancedMarkerOptions()
                .position(HO_CHI_MINH_CITY)
                .collisionBehavior(collisionBehavior)
                .title("Ho Chi Minh City (Collision Behavior)")
        )
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "advanced_markers",
    title = "Advanced Markers & Pins",
    description = "Modern PinConfig pins, custom glyphs, badge icon views, and collision behavior.",
    category = "Markers & Overlays",
    complexity = Complexity.ADVANCED,
    tags = {"#markers", "#advancedmarkers", "#pinconfig", "#collision", "#badges", "#mapid"},
    purpose = "Demonstrates Cloud-backed Advanced Markers with custom colors, pin glyphs, collision behaviors, and custom View icons.",
    successCriteria = "Custom colored pins and badge icon views render sharply at correct anchor points with collision handling.",
    failureIndicators = "Pins render as default red markers (missing Map ID), collision behavior ignored, or badge text blurry.",
    framework = Framework.JAVA_VIEWS
)
public class AdvancedMarkersDemoActivity extends SamplesBaseActivity implements OnMapReadyCallback {

    private static final LatLng SINGAPORE = new LatLng(1.3521, 103.8198);
    private static final LatLng KUALA_LUMPUR = new LatLng(3.1390, 101.6869);
    private static final LatLng JAKARTA = new LatLng(-6.2088, 106.8456);
    private static final LatLng BANGKOK = new LatLng(13.7563, 100.5018);
    private static final LatLng MANILA = new LatLng(14.5995, 120.9842);
    private static final LatLng HO_CHI_MINH_CITY = new LatLng(10.7769, 106.7009);

    private static final float ZOOM_LEVEL = 3.5f;

    private static final String TAG = AdvancedMarkersDemoActivity.class.getName();

    /**
     * This method is called when the activity is first created.
     *
     * It sets up the activity's layout and then initializes the map.
     *
     * The key logic here is to check if the developer has provided a Map ID in the
     * `strings.xml` file.
     *
     * If the `R.string.map_id` value is not the default "DEMO_MAP_ID", it means a
     * custom Map ID has been provided. In this case, we can rely on the simpler setup
     * where the `SupportMapFragment` is inflated directly from the XML layout, and it
     * will automatically use the Map ID from the string resource.
     *
     * However, if the `R.string.map_id` is still the default value, we fall back to a
     * programmatic setup. This involves:
     * 1. Retrieving the Map ID from the `secrets.properties` file, which is managed by the
     *    `ApiDemoApplication` class.
     * 2. Creating a `GoogleMapOptions` object.
     * 3. Explicitly setting the retrieved `mapId` on the `GoogleMapOptions`. This step is
     *    **critical** because Advanced Markers will not work without a valid Map ID.
     * 4. Creating a new `SupportMapFragment` instance with these options and replacing the
     *    placeholder fragment in the layout.
     *
     * This dual approach ensures that the demo can run seamlessly while also providing a
     * clear path for developers to use their own Map IDs, which is a requirement for using
     * Advanced Markers.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.advanced_markers_demo);

        if (!getString(com.example.common_ui.R.string.map_id).equals("DEMO_MAP_ID")) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        } else {
            String mapId = ((ApiDemoApplication) getApplication()).getMapId();
            if (mapId == null) {
                finish();
                return;
            }

            GoogleMapOptions mapOptions = new GoogleMapOptions().mapId(mapId);
            SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
            getSupportFragmentManager().beginTransaction()
                .replace(com.example.common_ui.R.id.map, mapFragment)
                .commit();
            mapFragment.getMapAsync(this);
        }

        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }



    @Override
    public void onMapReady(GoogleMap map) {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(SINGAPORE)
                .include(KUALA_LUMPUR)
                .include(JAKARTA)
                .include(BANGKOK)
                .include(MANILA)
                .include(HO_CHI_MINH_CITY)
                .build();
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Are advanced markers enabled? " + capabilities.isAdvancedMarkersAvailable());

        // 1. Custom View as iconView (Framed circular badge with Android logo)
        ImageView iconImageView = new ImageView(this);
        iconImageView.setImageResource(R.drawable.ic_android);
        iconImageView.setColorFilter(Color.parseColor("#3DDC84")); // Android Green
        iconImageView.setBackgroundResource(R.drawable.bg_marker_badge);
        int padding = (int) (8 * getResources().getDisplayMetrics().density);
        iconImageView.setPadding(padding, padding, padding, padding);
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        iconImageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        map.addMarker(new AdvancedMarkerOptions()
                .position(SINGAPORE)
                .iconView(iconImageView)
                .title("Singapore (Custom Framed Badge)")
                .zIndex(1f));

        // This uses PinConfig.Builder to create an instance of PinConfig.
        PinConfig.Builder pinConfigBuilder = PinConfig.builder();
        pinConfigBuilder.setBackgroundColor(Color.MAGENTA);
        PinConfig pinConfig = pinConfigBuilder.build();

        // Use the  PinConfig instance to set the icon for AdvancedMarkerOptions.
        AdvancedMarkerOptions advancedMarkerOptions = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig))
                .position(KUALA_LUMPUR);

        // Pass the AdvancedMarkerOptions instance to addMarker().
        Marker marker = map.addMarker(advancedMarkerOptions);

        // This sample changes the border color of the advanced marker
        PinConfig.Builder pinConfigBuilder2 = PinConfig.builder();
        pinConfigBuilder2.setBorderColor(Color.BLUE);
        PinConfig pinConfig2 = pinConfigBuilder2.build();

        AdvancedMarkerOptions advancedMarkerOptions2 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig2))
                .position(JAKARTA);

        Marker marker2 = map.addMarker(advancedMarkerOptions2);

        // Set the glyph text.
        PinConfig.Builder pinConfigBuilder3 = PinConfig.builder();
        PinConfig.Glyph glyphText = new PinConfig.Glyph("A");

        // Alternatively, you can set the text color:
        // Glyph glyphText = new Glyph("A", Color.GREEN);
        pinConfigBuilder3.setGlyph(glyphText);
        PinConfig pinConfig3 = pinConfigBuilder3.build();

        AdvancedMarkerOptions advancedMarkerOptions3 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig3))
                .position(BANGKOK);

        Marker marker3 = map.addMarker(advancedMarkerOptions3);

        // Create a transparent glyph.
        PinConfig.Builder pinConfigBuilder4 = PinConfig.builder();
        pinConfigBuilder4.setBackgroundColor(Color.MAGENTA);
        pinConfigBuilder4.setGlyph(new PinConfig.Glyph(Color.TRANSPARENT));
        PinConfig pinConfig4 = pinConfigBuilder4.build();

        AdvancedMarkerOptions advancedMarkerOptions4 = new AdvancedMarkerOptions()
                .icon(BitmapDescriptorFactory.fromPinConfig(pinConfig4))
                .position(MANILA);

        Marker marker4 = map.addMarker(advancedMarkerOptions4);

        // Collision behavior can only be changed in the AdvancedMarkerOptions object.
        // Changes to collision behavior after a marker has been created are not possible
        int collisionBehavior = AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL;
        AdvancedMarkerOptions advancedMarkerOptions5 = new AdvancedMarkerOptions()
                .position(HO_CHI_MINH_CITY)
                .collisionBehavior(collisionBehavior);

        Marker marker5 = map.addMarker(advancedMarkerOptions5);
    }
}
""".trimIndent()
        ),

        "com.example.kotlindemos.MarkerDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_marker",
            kotlinCode = """
@Sample(
    id = "marker_demo",
    title = "Standard Markers & Info Windows",
    description = "Placing markers, custom icons, draggable pins, and custom info window layouts.",
    category = "Markers & Overlays",
    complexity = Complexity.SIMPLE,
    tags = ["#markers", "#infowindow", "#draggable", "#icons", "#anchor"],
    purpose = "Demonstrates adding standard markers with alpha, rotation, draggable pins, and custom InfoWindowAdapter views.",
    successCriteria = "Tapping markers displays custom info windows with formatted content; dragging pins updates position.",
    failureIndicators = "Info window clicks not detected or custom snippet styling not applied.",
    framework = Framework.KOTLIN_VIEWS
)
class MarkerDemoActivity :
        SamplesBaseActivity(),
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        OnInfoWindowLongClickListener,
        OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private val TAG = MarkerDemoActivity::class.java.name

    /** This is ok to be lateinit as it is initialised in onMapReady */
    private lateinit var map: GoogleMap

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     *
     * Must be nullable as it is null when no marker has been selected
     */
    private var lastSelectedMarker: Marker? = null

    private val markerRainbow = ArrayList<Marker>()

    /** map to store place names and locations */
    private val places = mapOf(
            "BRISBANE" to LatLng(-27.47093, 153.0235),
            "MELBOURNE" to LatLng(-37.81319, 144.96298),
            "DARWIN" to LatLng(-12.4634, 130.8456),
            "SYDNEY" to LatLng(-33.87365, 151.20689),
            "ADELAIDE" to LatLng(-34.92873, 138.59995),
            "PERTH" to LatLng(-31.952854, 115.857342),
            "ALICE_SPRINGS" to LatLng(-24.6980, 133.8807)
    )

    private lateinit var binding: com.example.common_ui.databinding.MarkerDemoBinding

    private val random = Random()

    /** Demonstrates customizing the info window and/or its contents.  */
    internal inner class CustomInfoWindowAdapter : InfoWindowAdapter {

        // These are both view groups containing an ImageView with id "badge" and two
        // TextViews with id "title" and "snippet".
        private val window: View = layoutInflater.inflate(R.layout.custom_info_window, null)
        private val contents: View = layoutInflater.inflate(R.layout.custom_info_contents, null)

        override fun getInfoWindow(marker: Marker): View? {
            if (binding.customInfoWindowOptions.checkedRadioButtonId != R.id.custom_info_window) {
                // This means that getInfoContents will be called.
                return null
            }
            render(marker, window)
            return window
        }

        override fun getInfoContents(marker: Marker): View? {
            if (binding.customInfoWindowOptions.checkedRadioButtonId != R.id.custom_info_contents) {
                // This means that the default info contents will be used.
                return null
            }
            render(marker, contents)
            return contents
        }

        private fun render(marker: Marker, view: View) {
            val badge = when (marker.title!!) {
                "Brisbane" -> R.drawable.badge_qld
                "Adelaide" -> R.drawable.badge_sa
                "Sydney" -> R.drawable.badge_nsw
                "Melbourne" -> R.drawable.badge_victoria
                "Perth" -> R.drawable.badge_wa
                in "Darwin Marker 1".."Darwin Marker 4" -> R.drawable.badge_nt
                else -> 0 // Passing 0 to setImageResource will clear the image view.
            }

            view.findViewById<ImageView>(R.id.badge).setImageResource(badge)

            // Set the title and snippet for the custom info window
            val title: String? = marker.title
            val titleUi = view.findViewById<TextView>(R.id.title)

            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                titleUi.text = SpannableString(title).apply {
                    setSpan(ForegroundColorSpan(Color.RED), 0, length, 0)
                }
            } else {
                titleUi.text = ""
            }

            val snippet: String? = marker.snippet
            val snippetUi = view.findViewById<TextView>(R.id.snippet)
            if (snippet != null && snippet.length > 12) {
                snippetUi.text = SpannableString(snippet).apply {
                    setSpan(ForegroundColorSpan(Color.MAGENTA), 0, 10, 0)
                    setSpan(ForegroundColorSpan(Color.BLUE), 12, snippet.length, 0)
                }
            } else {
                snippetUi.text = ""
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = com.example.common_ui.databinding.MarkerDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rotationSeekBar.apply {
            max = 360
            setOnSeekBarChangeListener(object: OnSeekBarChangeListener {

                /** Called when the Rotation progress bar is moved */
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val rotation = seekBar?.progress?.toFloat()
                    checkReadyThen { markerRainbow.map { it.rotation = rotation ?: 0f } }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    // do nothing
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    //do nothing
                }

            } )
        }

        binding.customInfoWindowOptions.apply {
            setOnCheckedChangeListener { _, _ ->
                if (lastSelectedMarker?.isInfoWindowShown == true) {
                    // Refresh the info window when the info window's content has changed.
                    // must deal with the possibility that lastSelectedMarker has changed in
                    // another thread between the null check and this line, do this with !!
                    lastSelectedMarker?.showInfoWindow()
                }
            }
        }

        binding.clearMap.setOnClickListener { onClearMap() }
        binding.resetMap.setOnClickListener { onResetMap() }
        binding.flat.setOnClickListener { onToggleFlat() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        OnMapAndViewReadyListener(mapFragment, this)
        applyInsets(binding.mapContainer)
    }

    /**
     * This is the callback that is triggered when the GoogleMap has loaded and is ready for use
     */
    override fun onMapReady(googleMap: GoogleMap?) {

        // return early if the map was not initialised properly
        map = googleMap ?: return

        // create bounds that encompass every location we reference
        val boundsBuilder = LatLngBounds.Builder()
        // include all places we have markers for on the map
        places.keys.map { place -> boundsBuilder.include(places.getValue(place)) }
        val bounds = boundsBuilder.build()

        with(map) {
            // Hide the zoom controls as the button panel will cover it.
            uiSettings.isZoomControlsEnabled = false

            // Setting an info window adapter allows us to change the both the contents and
            // look of the info window.
            setInfoWindowAdapter(CustomInfoWindowAdapter())

            // Set listeners for marker events.  See the bottom of this class for their behavior.
            setOnMarkerClickListener(this@MarkerDemoActivity)
            setOnInfoWindowClickListener(this@MarkerDemoActivity)
            setOnMarkerDragListener(this@MarkerDemoActivity)
            setOnInfoWindowCloseListener(this@MarkerDemoActivity)
            setOnInfoWindowLongClickListener(this@MarkerDemoActivity)

            // Override the default content description on the view, for accessibility mode.
            // Ideally this string would be localised.
            setContentDescription("Map with lots of markers.")

            moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }

        // Add lots of markers to the googleMap.
        addMarkersToMap()

    }

    /**
     * Show all the specified markers on the map
     */
    private fun addMarkersToMap() {

        val placeDetailsMap = mutableMapOf(
                // Uses a coloured icon
                "BRISBANE" to PlaceDetails(
                        position = places.getValue("BRISBANE"),
                        title = "Brisbane",
                        snippet = "Population: 2,074,200",
                        icon = BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                ),

                // Uses a custom icon with the info window popping out of the center of the icon.
                "SYDNEY" to PlaceDetails(
                        position = places.getValue("SYDNEY"),
                        title = "Sydney",
                        snippet = "Population: 4,627,300",
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.arrow),
                        infoWindowAnchorX = 0.5f,
                        infoWindowAnchorY = 0.5f
                ),

                // Will create a draggable marker. Long press to drag.
                "MELBOURNE" to PlaceDetails(
                        position = places.getValue("MELBOURNE"),
                        title = "Melbourne",
                        snippet = "Population: 4,137,400",
                        draggable = true
                ),

                // Use a vector drawable resource as a marker icon.
                "ALICE_SPRINGS" to PlaceDetails(
                        position = places.getValue("ALICE_SPRINGS"),
                        title = "Alice Springs",
                        icon = vectorToBitmap(
                                R.drawable.ic_android, "#A4C639".toColorInt())
                ),

                // More markers for good measure
                "PERTH" to PlaceDetails(
                        position = places.getValue("PERTH"),
                        title = "Perth",
                        snippet = "Population: 1,738,800"
                ),

                "ADELAIDE" to PlaceDetails(
                        position = places.getValue("ADELAIDE"),
                        title = "Adelaide",
                        snippet = "Population: 1,213,000"
                )

        )

        // add 4 markers on top of each other in Darwin with varying z-indexes
        (0 until 4).map {
            placeDetailsMap.put(
                "DARWIN ${"$"}{it + 1}", PlaceDetails(
                    position = places.getValue("DARWIN"),
                    title = "Darwin Marker ${"$"}{it + 1}",
                    snippet = "z-index initially ${"$"}{it + 1}",
                    zIndex = it.toFloat()
                )
            )
        }

        // place markers for each of the defined locations
        placeDetailsMap.keys.map {
            with(placeDetailsMap.getValue(it)) {
                map.addMarker(MarkerOptions()
                        .position(position)
                        .title(title)
                        .snippet(snippet)
                        .icon(icon)
                        .infoWindowAnchor(infoWindowAnchorX, infoWindowAnchorY)
                        .draggable(draggable)
                        .zIndex(zIndex))

            }
        }

        // Creates a marker rainbow demonstrating how to create default marker icons of different
        // hues (colors).
        val numMarkersInRainbow = 12
        (0 until numMarkersInRainbow).mapTo(markerRainbow) {
            map.addMarker(MarkerOptions().apply{
                position(LatLng(
                    -30 + 10 * sin(it * Math.PI / (numMarkersInRainbow - 1)),
                    135 - 10 * cos(it * Math.PI / (numMarkersInRainbow - 1))
                ))
                title("Marker ${"$"}it")
                icon(BitmapDescriptorFactory.defaultMarker((it * 360 / numMarkersInRainbow)
                                                               .toFloat()))
                flat(binding.flat.isChecked)
                rotation(binding.rotationSeekBar.progress.toFloat())
            })!!
        }
    }

    /**
     * Demonstrates converting a [Drawable] to a [BitmapDescriptor],
     * for use as a marker icon.
     */
    private fun vectorToBitmap(@DrawableRes id : Int, @ColorInt color : Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e(TAG, "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun onClearMap() {
        checkReadyThen { map.clear() }
    }

    private fun onResetMap() {
        checkReadyThen {
            map.clear()
            addMarkersToMap()
        }
    }

    private fun onToggleFlat() {
        checkReadyThen { markerRainbow.map { marker -> marker.isFlat = binding.flat.isChecked } }
    }

    //
    // Marker related listeners.
    //
    override fun onMarkerClick(marker : Marker): Boolean {

        // Markers have a z-index that is settable and gettable.
        marker.zIndex += 1.0f
        Toast.makeText(this, "${"$"}{marker.title} z-index set to ${"$"}{marker.zIndex}",
                Toast.LENGTH_SHORT).show()

        lastSelectedMarker = marker

        if (marker.position == places.getValue("PERTH")) {
            // This causes the marker at Perth to bounce into position when it is clicked.
            val handler = Handler(Looper.getMainLooper())
            val start = SystemClock.uptimeMillis()
            val duration = 1500

            val interpolator = BounceInterpolator()

            handler.post(object : Runnable {
                override fun run() {
                    val elapsed = SystemClock.uptimeMillis() - start
                    val t =
                        (1 - interpolator.getInterpolation(elapsed.toFloat() / duration)).coerceAtLeast(
                            0f
                        )
                    marker.setAnchor(0.5f, 1.0f + 2 * t)

                    // Post again 16ms later.
                    if (t > 0.0) {
                        handler.postDelayed(this, 16)
                    }
                }
            })
        } else if (marker.position == places.getValue("ADELAIDE")) {
            // This causes the marker at Adelaide to change color and alpha.
            marker.apply {
                setIcon(BitmapDescriptorFactory.defaultMarker(random.nextFloat() * 360))
                alpha = random.nextFloat()
            }
        }

        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false
    }

    override fun onInfoWindowClick(marker : Marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowClose(marker : Marker) {
        Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show()
    }

    override fun onInfoWindowLongClick(marker : Marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerDragStart(marker : Marker) {
        binding.topText.text = getString(R.string.on_marker_drag_start)
    }

    override fun onMarkerDragEnd(marker : Marker) {
        binding.topText.text = getString(R.string.on_marker_drag_end)
    }

    override fun onMarkerDrag(marker : Marker) {
        binding.topText.text = getString(R.string.on_marker_drag, marker.position.latitude, marker.position.longitude)
    }

    /**
     * Checks if the map is ready, the executes the provided lambda function
     *
     * @param stuffToDo the code to be executed if the map is ready
     */
    private fun checkReadyThen(stuffToDo : () -> Unit) {
        if (!::map.isInitialized) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show()
        } else {
            stuffToDo()
        }
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "marker_demo",
    title = "Standard Markers & Info Windows",
    description = "Placing markers, custom icons, draggable pins, and custom info window layouts.",
    category = "Markers & Overlays",
    complexity = Complexity.SIMPLE,
    tags = {"#markers", "#infowindow", "#draggable", "#icons", "#anchor"},
    purpose = "Demonstrates adding standard markers with alpha, rotation, draggable pins, and custom InfoWindowAdapter views.",
    successCriteria = "Tapping markers displays custom info windows with formatted content; dragging pins updates position.",
    failureIndicators = "Info window clicks not detected or custom snippet styling not applied.",
    framework = Framework.JAVA_VIEWS
)
public class MarkerDemoActivity extends SamplesBaseActivity implements
        OnMarkerClickListener,
        OnInfoWindowClickListener,
        OnMarkerDragListener,
        OnSeekBarChangeListener,
        OnInfoWindowLongClickListener,
        OnInfoWindowCloseListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);

    private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    private static final LatLng DARWIN = new LatLng(-12.4634, 130.8456);

    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);

    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);

    private static final LatLng ALICE_SPRINGS = new LatLng(-24.6980, 133.8807);

    private com.example.common_ui.databinding.MarkerDemoBinding binding;

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (binding.customInfoWindowOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
                // This means that getInfoContents will be called.
                return null;
            }
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            if (binding.customInfoWindowOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
                // This means that the default info contents will be used.
                return null;
            }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else if (marker.equals(mDarwin1)) {
                badge = R.drawable.badge_nt;
            } else if (marker.equals(mDarwin2)) {
                badge = R.drawable.badge_nt;
            } else if (marker.equals(mDarwin3)) {
                badge = R.drawable.badge_nt;
            } else if (marker.equals(mDarwin4)) {
                badge = R.drawable.badge_nt;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = view.findViewById(R.id.title);
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = view.findViewById(R.id.snippet);
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }

    private GoogleMap mMap;

    private Marker mPerth;

    private Marker mSydney;

    private Marker mBrisbane;

    private Marker mAdelaide;

    private Marker mMelbourne;

    private Marker mDarwin1;
    private Marker mDarwin2;
    private Marker mDarwin3;
    private Marker mDarwin4;


    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */
    private Marker mLastSelectedMarker;

    private final List<Marker> mMarkerRainbow = new ArrayList<>();

    private final Random mRandom = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.common_ui.databinding.MarkerDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rotationSeekBar.setMax(360);
        binding.rotationSeekBar.setOnSeekBarChangeListener(this);

        binding.customInfoWindowOptions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mLastSelectedMarker != null && mLastSelectedMarker.isInfoWindowShown()) {
                    // Refresh the info window when the info window's content has changed.
                    mLastSelectedMarker.showInfoWindow();
                }
            }
        });

        binding.clearMap.setOnClickListener(v -> onClearMap());
        binding.resetMap.setOnClickListener(v -> onResetMap());
        binding.flat.setOnClickListener(v -> onToggleFlat());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);

        applyInsets(binding.mapContainer);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(PERTH)
                .include(SYDNEY)
                .include(ADELAIDE)
                .include(BRISBANE)
                .include(MELBOURNE)
                .include(DARWIN)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
    }

    private void addMarkersToMap() {
        // Uses a colored icon.
        mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
                .snippet("Population: 2,074,200")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        // Uses a custom icon with the info window popping out of the center of the icon.
        mSydney = mMap.addMarker(new MarkerOptions()
                .position(SYDNEY)
                .title("Sydney")
                .snippet("Population: 4,627,300")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
                .infoWindowAnchor(0.5f, 0.5f));

        // Creates a draggable marker. Long press to drag.
        mMelbourne = mMap.addMarker(new MarkerOptions()
                .position(MELBOURNE)
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .draggable(true));

        // Place four markers on top of each other with differing z-indexes.
        mDarwin1 = mMap.addMarker(new MarkerOptions()
                .position(DARWIN)
                .title("Darwin Marker 1")
                .snippet("z-index 1")
                .zIndex(1));
        mDarwin2 = mMap.addMarker(new MarkerOptions()
                .position(DARWIN)
                .title("Darwin Marker 2")
                .snippet("z-index 2")
                .zIndex(2));
        mDarwin3 = mMap.addMarker(new MarkerOptions()
                .position(DARWIN)
                .title("Darwin Marker 3")
                .snippet("z-index 3")
                .zIndex(3));
        mDarwin4 = mMap.addMarker(new MarkerOptions()
                .position(DARWIN)
                .title("Darwin Marker 4")
                .snippet("z-index 4")
                .zIndex(4));


        // A few more markers for good measure.
        mPerth = mMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .title("Perth")
                .snippet("Population: 1,738,800"));
        mAdelaide = mMap.addMarker(new MarkerOptions()
                .position(ADELAIDE)
                .title("Adelaide")
                .snippet("Population: 1,213,000"));

        // Vector drawable resource as a marker icon.
        mMap.addMarker(new MarkerOptions()
                .position(ALICE_SPRINGS)
                .icon(vectorToBitmap(R.drawable.ic_android, Color.parseColor("#A4C639")))
                .title("Alice Springs"));

        // Creates a marker rainbow demonstrating how to create default marker icons of different
        // hues (colors).
        float rotation = binding.rotationSeekBar.getProgress();
        boolean flat = binding.flat.isChecked();

        int numMarkersInRainbow = 12;
        for (int i = 0; i < numMarkersInRainbow; i++) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1)),
                            135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1))))
                    .title("Marker " + i)
                    .icon(BitmapDescriptorFactory.defaultMarker(i * 360 / numMarkersInRainbow))
                    .flat(flat)
                    .rotation(rotation));
            mMarkerRainbow.add(marker);
        }
    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void onClearMap() {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    private void onResetMap() {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        addMarkersToMap();
    }

    private void onToggleFlat() {
        if (!checkReady()) {
            return;
        }
        boolean flat = binding.flat.isChecked();
        for (Marker marker : mMarkerRainbow) {
            marker.setFlat(flat);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!checkReady()) {
            return;
        }
        float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing.
    }

    //
    // Marker related listeners.
    //

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(mPerth)) {
            // This causes the marker at Perth to bounce into position when it is clicked.
            final Handler handler = new Handler(Looper.getMainLooper());
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(
                            1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
        } else if (marker.equals(mAdelaide)) {
            // This causes the marker at Adelaide to change color and alpha.
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(mRandom.nextFloat() * 360));
            marker.setAlpha(mRandom.nextFloat());
        }

        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        Toast.makeText(this, marker.getTitle() + " z-index set to " + zIndex,
                Toast.LENGTH_SHORT).show();

        mLastSelectedMarker = marker;
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Click Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        //Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        binding.topText.setText(R.string.on_marker_drag_start);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        binding.topText.setText(R.string.on_marker_drag_end);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        binding.topText.setText(getString(R.string.on_marker_drag, marker.getPosition().latitude, marker.getPosition().longitude));
    }

}
""".trimIndent()
        ),

        "com.example.kotlindemos.EventsDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_events",
            kotlinCode = """
@Sample(
    id = "events_demo",
    title = "Events & Gestures",
    description = "Handling map taps, long clicks, camera change events, and POI selections.",
    category = "Events & Gestures",
    complexity = Complexity.SNIPPET,
    tags = ["#events", "#gestures", "#clicks", "#poi", "#listeners"],
    purpose = "Demonstrates registering listeners for map clicks, long presses, camera moves, and POI selections.",
    successCriteria = "Event log text updates with coordinates and POI names upon user interaction.",
    failureIndicators = "Click events swallowed or POI name unresolved.",
    framework = Framework.KOTLIN_VIEWS
)
class EventsDemoActivity : SamplesBaseActivity(), OnMapClickListener,
    OnMapLongClickListener, OnCameraIdleListener, OnCameraMoveListener, OnMapReadyCallback {

    private lateinit var tapTextView: TextView
    private lateinit var cameraTextView: TextView
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_demo)
        tapTextView = findViewById(R.id.tap_text)
        cameraTextView = findViewById(R.id.camera_text)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        applyInsets(findViewById(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        map.setOnCameraMoveListener(this)
        map.setOnCameraIdleListener(this)
        updateCameraPosition()
    }

    override fun onMapClick(point: LatLng) {
        val lat = String.format(Locale.US, "%.6f", point.latitude)
        val lng = String.format(Locale.US, "%.6f", point.longitude)
        tapTextView.text = getString(R.string.events_tapped_format, lat, lng)
    }

    override fun onMapLongClick(point: LatLng) {
        val lat = String.format(Locale.US, "%.6f", point.latitude)
        val lng = String.format(Locale.US, "%.6f", point.longitude)
        tapTextView.text = getString(R.string.events_long_pressed_format, lat, lng)
    }

    override fun onCameraMove() {
        updateCameraPosition()
    }

    override fun onCameraIdle() {
        updateCameraPosition()
    }

    private fun updateCameraPosition() {
        if (!::map.isInitialized) return
        val pos = map.cameraPosition
        val lat = String.format(Locale.US, "%.6f", pos.target.latitude)
        val lng = String.format(Locale.US, "%.6f", pos.target.longitude)
        val zoom = String.format(Locale.US, "%.1f", pos.zoom)
        val tilt = String.format(Locale.US, "%.1f", pos.tilt)
        val bearing = String.format(Locale.US, "%.1f", pos.bearing)
        cameraTextView.text = getString(R.string.events_camera_position_format, lat, lng, zoom, tilt, bearing)
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "events_demo",
    title = "Events & Gestures",
    description = "Handling map taps, long clicks, camera change events, and POI selections.",
    category = "Events & Gestures",
    complexity = Complexity.SNIPPET,
    tags = {"#events", "#gestures", "#clicks", "#poi", "#listeners"},
    purpose = "Demonstrates registering listeners for map clicks, long presses, camera moves, and POI selections.",
    successCriteria = "Event log text updates with coordinates and POI names upon user interaction.",
    failureIndicators = "Click events swallowed or POI name unresolved.",
    framework = Framework.JAVA_VIEWS
)
public class EventsDemoActivity extends SamplesBaseActivity
        implements OnMapClickListener, OnMapLongClickListener, OnCameraIdleListener,
        GoogleMap.OnCameraMoveListener, OnMapReadyCallback {

    private TextView tapTextView;
    private TextView cameraTextView;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.events_demo);

        tapTextView = findViewById(com.example.common_ui.R.id.tap_text);
        cameraTextView = findViewById(com.example.common_ui.R.id.camera_text);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.map.setOnMapClickListener(this);
        this.map.setOnMapLongClickListener(this);
        this.map.setOnCameraMoveListener(this);
        this.map.setOnCameraIdleListener(this);
        updateCameraPosition();
    }

    @Override
    public void onMapClick(LatLng point) {
        String lat = String.format(Locale.US, "%.6f", point.latitude);
        String lng = String.format(Locale.US, "%.6f", point.longitude);
        tapTextView.setText(getString(com.example.common_ui.R.string.events_tapped_format, lat, lng));
    }

    @Override
    public void onMapLongClick(LatLng point) {
        String lat = String.format(Locale.US, "%.6f", point.latitude);
        String lng = String.format(Locale.US, "%.6f", point.longitude);
        tapTextView.setText(getString(com.example.common_ui.R.string.events_long_pressed_format, lat, lng));
    }

    @Override
    public void onCameraMove() {
        updateCameraPosition();
    }

    @Override
    public void onCameraIdle() {
        updateCameraPosition();
    }

    private void updateCameraPosition() {
        if (map == null) return;
        com.google.android.gms.maps.model.CameraPosition pos = map.getCameraPosition();
        String lat = String.format(Locale.US, "%.6f", pos.target.latitude);
        String lng = String.format(Locale.US, "%.6f", pos.target.longitude);
        String zoom = String.format(Locale.US, "%.1f", pos.zoom);
        String tilt = String.format(Locale.US, "%.1f", pos.tilt);
        String bearing = String.format(Locale.US, "%.1f", pos.bearing);
        cameraTextView.setText(getString(
            com.example.common_ui.R.string.events_camera_position_format,
            lat,
            lng,
            zoom,
            tilt,
            bearing
        ));
    }
}
""".trimIndent()
        ),

        "com.example.kotlindemos.CameraDemoActivity" to SnippetPair(
            regionTag = "maps_camera_events",
            kotlinCode = """
@Sample(
    id = "camera_demo",
    title = "Camera Controls & Animation",
    description = "Programmatic camera panning, zooming, tilt, bearing, and smooth animations.",
    category = "Camera Controls",
    complexity = Complexity.SIMPLE,
    tags = ["#camera", "#animation", "#bearing", "#tilt", "#zoom", "#pan"],
    purpose = "Demonstrates programmatic camera movements, animated transitions, tilt angles, and bearing rotations.",
    successCriteria = "Buttons animate camera smoothly with custom durations, stops, and rotation angles.",
    failureIndicators = "Jerky animations, unexpected camera jumps, or tilt angle exceeding platform constraints.",
    framework = Framework.KOTLIN_VIEWS
)
class CameraDemoActivity :
        SamplesBaseActivity(),
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
    

    private lateinit var map: GoogleMap
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        applyInsets(binding.mapContainer)

        binding.bondi.setOnClickListener(this::onGoToBondi)
        binding.sydney.setOnClickListener(this::onGoToSydney)
        binding.stopAnimation.setOnClickListener(this::onStopAnimation)
        binding.animate.setOnClickListener(this::onToggleAnimate)
        binding.scrollLeft.setOnClickListener(this::onScrollLeft)
        binding.scrollUp.setOnClickListener(this::onScrollUp)
        binding.scrollDown.setOnClickListener(this::onScrollDown)
        binding.scrollRight.setOnClickListener(this::onScrollRight)
        binding.zoomIn.setOnClickListener(this::onZoomIn)
        binding.zoomOut.setOnClickListener(this::onZoomOut)
        binding.tiltMore.setOnClickListener(this::onTiltMore)
        binding.tiltLess.setOnClickListener(this::onTiltLess)
        binding.durationToggle.setOnClickListener(this::onToggleCustomDuration)
    }

    

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        // return early if the map was not initialised properly
        with(googleMap) {
            setOnCameraIdleListener(this@CameraDemoActivity)
            setOnCameraMoveStartedListener(this@CameraDemoActivity)
            setOnCameraMoveListener(this@CameraDemoActivity)
            setOnCameraMoveCanceledListener(this@CameraDemoActivity)
            

            // Show Sydney
            moveCamera(CameraUpdateFactory.newLatLngZoom(sydneyLatLng, 10f))
        }
    }

    

    override fun onCameraMoveStarted(reason: Int) {
        

        var reasonText = "UNKNOWN_REASON"
        
        when (reason) {
            OnCameraMoveStartedListener.REASON_GESTURE -> {
                
                reasonText = "GESTURE"
            }
            OnCameraMoveStartedListener.REASON_API_ANIMATION -> {
                
                reasonText = "API_ANIMATION"
            }
            OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> {
                
                reasonText = "DEVELOPER_ANIMATION"
            }
        }
        Log.d(TAG, "onCameraMoveStarted(${"$"}reasonText)")
        
    }

    

    override fun onCameraMove() {
        Log.d(TAG, "onCameraMove")
        
    }

    override fun onCameraMoveCanceled() {
        
        Log.d(TAG, "onCameraMoveCancelled")
    }

    override fun onCameraIdle() {
        
        Log.d(TAG, "onCameraIdle")
    }
    
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "camera_demo",
    title = "Camera Controls & Animation",
    description = "Programmatic camera panning, zooming, tilt, bearing, and smooth animations.",
    category = "Camera Controls",
    complexity = Complexity.SIMPLE,
    tags = {"#camera", "#animation", "#bearing", "#tilt", "#zoom", "#pan"},
    purpose = "Demonstrates programmatic camera movements, animated transitions, tilt angles, and bearing rotations.",
    successCriteria = "Buttons animate camera smoothly with custom durations, stops, and rotation angles.",
    failureIndicators = "Jerky animations, unexpected camera jumps, or tilt angle exceeding platform constraints.",
    framework = Framework.JAVA_VIEWS
)
public class CameraDemoActivity extends SamplesBaseActivity implements
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener,
        OnMapReadyCallback {
    

    private GoogleMap map;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CameraDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(binding.mapContainer);

        binding.bondi.setOnClickListener(this::onGoToBondi);
        binding.sydney.setOnClickListener(this::onGoToSydney);
        binding.stopAnimation.setOnClickListener(this::onStopAnimation);
        binding.animate.setOnClickListener(this::onToggleAnimate);
        binding.scrollLeft.setOnClickListener(this::onScrollLeft);
        binding.scrollUp.setOnClickListener(this::onScrollUp);
        binding.scrollDown.setOnClickListener(this::onScrollDown);
        binding.scrollRight.setOnClickListener(this::onScrollRight);
        binding.zoomIn.setOnClickListener(this::onZoomIn);
        binding.zoomOut.setOnClickListener(this::onZoomOut);
        binding.tiltMore.setOnClickListener(this::onTiltMore);
        binding.tiltLess.setOnClickListener(this::onTiltLess);
        binding.durationToggle.setOnClickListener(this::onToggleCustomDuration);
    }

    

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);
        

        // Show Sydney
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 10));
    }

    public GoogleMap getMap() {
        return map;
    }

    

    @Override
    public void onCameraMoveStarted(int reason) {
        

        String reasonText = "UNKNOWN_REASON";
        
        switch (reason) {
            case OnCameraMoveStartedListener.REASON_GESTURE:
                
                reasonText = "GESTURE";
                break;
            case OnCameraMoveStartedListener.REASON_API_ANIMATION:
                
                reasonText = "API_ANIMATION";
                break;
            case OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:
                
                reasonText = "DEVELOPER_ANIMATION";
                break;
        }
        Log.d(TAG, "onCameraMoveStarted(" + reasonText + ")");
        
    }

    @Override
    public void onCameraMove() {
        
        Log.d(TAG, "onCameraMove");
    }

    @Override
    public void onCameraMoveCanceled() {
        
        Log.d(TAG, "onCameraMoveCancelled");
    }

    @Override
    public void onCameraIdle() {
        
        Log.d(TAG, "onCameraIdle");
    }

    
}
""".trimIndent()
        ),

        "com.example.kotlindemos.MyLocationDemoActivity" to SnippetPair(
            regionTag = "maps_android_sample_my_location",
            kotlinCode = """
@Sample(
    id = "my_location",
    title = "My Location Layer",
    description = "Enabling blue dot location indicator and My Location button with runtime permissions.",
    category = "Location & Sensors",
    complexity = Complexity.SIMPLE,
    tags = ["#location", "#mylocation", "#permissions", "#bluedot"],
    purpose = "Demonstrates requesting ACCESS_FINE_LOCATION permissions and enabling the blue dot location layer.",
    successCriteria = "Tapping My Location button centers camera on user's current GPS position.",
    failureIndicators = "Permission denial causes unhandled crash or location button missing.",
    framework = Framework.KOTLIN_VIEWS
)
class MyLocationDemoActivity : SamplesBaseActivity(),
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener, OnMapReadyCallback,
    OnRequestPermissionsResultCallback {
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * [.onRequestPermissionsResult].
     */
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_location_demo)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        applyInsets(findViewById(R.id.map_container))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {

        // [START maps_check_location_permission]
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }

        // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
        // [END maps_check_location_permission]
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(this, "Current location:\n${"$"}location", Toast.LENGTH_LONG)
            .show()
    }

    // [START maps_check_location_permission_result]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
            return
        }

        if (isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) || isPermissionGranted(
                permissions,
                grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation()
        } else {
            // Permission was denied. Display an error message
            
        }
    }

    // [END maps_check_location_permission_result]
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError()
            permissionDenied = false
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private fun showMissingPermissionError() {
        newInstance(true).show(supportFragmentManager, "dialog")
    }

    companion object {
        /**
         * Request code for location permission request.
         *
         * @see .onRequestPermissionsResult
         */
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "my_location",
    title = "My Location Layer",
    description = "Enabling blue dot location indicator and My Location button with runtime permissions.",
    category = "Location & Sensors",
    complexity = Complexity.SIMPLE,
    tags = {"#location", "#mylocation", "#permissions", "#bluedot"},
    purpose = "Demonstrates requesting ACCESS_FINE_LOCATION permissions and enabling the blue dot location layer.",
    successCriteria = "Tapping My Location button centers camera on user's current GPS position.",
    failureIndicators = "Permission denial causes unhandled crash or location button missing.",
    framework = Framework.JAVA_VIEWS
)
public class MyLocationDemoActivity extends SamplesBaseActivity
    implements
    OnMyLocationButtonClickListener,
    OnMyLocationClickListener,
    OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.common_ui.R.layout.my_location_demo);

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(com.example.common_ui.R.id.map);
        mapFragment.getMapAsync(this);
        applyInsets(findViewById(com.example.common_ui.R.id.map_container));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
        // [END maps_check_location_permission]
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
            Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtils
            .isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
            .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
""".trimIndent()
        ),

        "com.example.kotlindemos.DataDrivenBoundariesActivity" to SnippetPair(
            regionTag = "maps_android_data_driven_styling_boundaries",
            kotlinCode = """
// Add PopupMenu.OnMenuItemClickListener interface
@Sample(
    id = "data_driven_boundaries",
    title = "Data-Driven Boundaries",
    description = "Dynamic styling and click handlers for administrative boundaries (Localities, States, Countries).",
    category = "Data-Driven Styling",
    complexity = Complexity.ADVANCED,
    tags = ["#boundaries", "#datadriven", "#featurelayer", "#locality", "#choropleth"],
    purpose = "Demonstrates styling administrative boundaries dynamically via FeatureLayer and capturing boundary clicks.",
    successCriteria = "Boundaries render with custom stroke and fill colors; tapping a region highlights its polygon.",
    failureIndicators = "Boundary layer is null (requires vector map / Map ID) or click listener not firing.",
    framework = Framework.KOTLIN_VIEWS
)
class DataDrivenBoundariesActivity : SamplesBaseActivity(), OnMapReadyCallback,
    FeatureLayer.OnFeatureClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var map: GoogleMap

    private var localityLayer: FeatureLayer? = null
    private var areaLevel1Layer: FeatureLayer? = null
    private var countryLayer: FeatureLayer? = null

    private val HANA_HAWAII = LatLng(20.7522, -155.9877) // Hana, Hawaii
    private val CENTER_US = LatLng(39.8283, -98.5795) // Approx center US

    // --- State Variables ---
    private var localityEnabled = true // Default enabled
    private var adminAreaEnabled = false
    private var countryEnabled = false
    private val selectedPlaceIds = mutableSetOf<String>() // For selected countries

    // --- Style Factories (defined once) ---
    private val localityStyleFactory: FeatureLayer.StyleFactory = createLocalityStyleFactory()
    private val areaLevel1StyleFactory: FeatureLayer.StyleFactory = createAreaLevel1StyleFactory()
    // Country factory references selectedPlaceIds, needs to be instance property or re-created if needed
    private val countryStyleFactory: FeatureLayer.StyleFactory = createCountryStyleFactory()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Assumes layout is in common_ui module
        setContentView(R.layout.data_driven_boundaries_demo)

        val mapId = (application as ApiDemoApplication).mapId

        // --- Map ID Check ---
        if (mapId == null) {
            finish()
            return // Exit early if no valid Map ID
        }

        // --- Programmatically create and add the map fragment ---
        val mapOptions = GoogleMapOptions().apply {
            mapId(mapId)
        }
        val mapFragment = SupportMapFragment.newInstance(mapOptions)
        supportFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, mapFragment) // Use the container ID
            .commit()
        mapFragment.getMapAsync(this)

        // --- Setup Buttons ---
        findViewById<MaterialButton>(R.id.button_hawaii).setOnClickListener {
            centerMapOnLocation(HANA_HAWAII, 11f) // Adjusted zoom from Java
        }
        findViewById<MaterialButton>(R.id.button_us).setOnClickListener {
            centerMapOnLocation(CENTER_US, 1f) // Adjusted zoom from Java
        }
        setupBoundarySelectorButton() // Setup the new selector button

        // --- Insets ---
        applyInsets(findViewById(R.id.map_container)) // Apply insets if needed
    }

    private fun setupBoundarySelectorButton() {
        val stylingTypeButton: MaterialButton = findViewById(R.id.button_feature_type) // Find the button
        stylingTypeButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.boundary_types_menu, popupMenu.menu) // Inflate your menu

            popupMenu.setOnMenuItemClickListener(this) // Set listener to this Activity

            // Set initial check states based on current flags
            popupMenu.menu.findItem(R.id.boundary_type_locality)?.isChecked = localityEnabled
            popupMenu.menu.findItem(R.id.boundary_type_administrative_area_level_1)?.isChecked = adminAreaEnabled
            popupMenu.menu.findItem(R.id.boundary_type_country)?.isChecked = countryEnabled

            popupMenu.show()
        }
    }

    private fun centerMapOnLocation(location: LatLng, zoomLevel: Float) {
        if (::map.isInitialized) { // Check if map is ready
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
        } else {
            Log.w(TAG, "Map not initialized, cannot center map.")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val capabilities: MapCapabilities = map.mapCapabilities
        Log.d(TAG, "Data-driven Styling is available: ${"$"}{capabilities.isDataDrivenStylingAvailable}")

        if (!capabilities.isDataDrivenStylingAvailable) {
            Toast.makeText(
                this,
                "Data-driven Styling is not available. See README.md for instructions.",
                Toast.LENGTH_LONG
            ).show()
        }

        // Get feature layers
        localityLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.LOCALITY)
                .build()
        )
        areaLevel1Layer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                .build()
        )
        countryLayer = googleMap.getFeatureLayer(
            FeatureLayerOptions.Builder()
                .featureType(FeatureType.COUNTRY)
                .build()
        ).also {
            it.addOnFeatureClickListener(this)
        }

        // Apply initial styles based on default flags
        updateStyles()

        // Center map initially
        centerMapOnLocation(HANA_HAWAII, 11f)
    }

    /**
     * Updates the styles based on the enabled flags.
     */
    private fun updateStyles() {
        Log.d(TAG, "Updating Styles: Locality=${"$"}localityEnabled, Admin1=${"$"}adminAreaEnabled, Country=${"$"}countryEnabled")
        localityLayer?.featureStyle = if (localityEnabled) localityStyleFactory else null
        areaLevel1Layer?.featureStyle = if (adminAreaEnabled) areaLevel1StyleFactory else null
        countryLayer?.featureStyle = if (countryEnabled) countryStyleFactory else null
    }

    // --- Style Factory Creation Methods ---

    private fun createLocalityStyleFactory(): FeatureLayer.StyleFactory {
        val purple = 0x810FCB
        // Define a style with purple fill at 50% opacity and solid purple border.
        val fillColor = ColorUtils.setAlphaComponent(purple, (0.5f * 255).roundToInt())
        val strokeColor = ColorUtils.setAlphaComponent(purple, 255) // Fully opaque

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature && feature.placeId == "ChIJ0zQtYiWsVHkRk8lRoB1RNPo") { // Hana, HI
                FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .build()
            } else {
                null // No style for other localities
            }
        }
    }

    private fun createAreaLevel1StyleFactory(): FeatureLayer.StyleFactory {
        val alpha = (255 * 0.25).roundToInt() // 25% opacity

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature) {
                // Generate a hue based on placeId hash
                var hueColor = feature.placeId.hashCode() % 300
                if (hueColor < 0) hueColor += 300
                FeatureStyle.Builder()
                    .fillColor(Color.HSVToColor(alpha, floatArrayOf(hueColor.toFloat(), 1f, 1f)))
                    .build()
            } else {
                null
            }
        }
    }

    private fun createCountryStyleFactory(): FeatureLayer.StyleFactory {
        val defaultFillColor = ColorUtils.setAlphaComponent(Color.BLACK, (0.1f * 255).roundToInt()) // 10% Black
        val selectedFillColor = ColorUtils.setAlphaComponent(Color.RED, (0.33f * 255).roundToInt()) // 33% Red

        return FeatureLayer.StyleFactory { feature ->
            if (feature is PlaceFeature) {
                // Check if this country's place ID is in our selected set
                val fillColor = if (selectedPlaceIds.contains(feature.placeId)) {
                    selectedFillColor
                } else {
                    defaultFillColor
                }
                FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(Color.BLACK) // Solid black border
                    .build()
            } else {
                null
            }
        }
    }

    // --- Listener Implementations ---

    /**
     * Handles clicks on the Country Layer features.
     */
    override fun onFeatureClick(event: FeatureClickEvent) {
        val clickedPlaceIds = event.features
            .filterIsInstance<PlaceFeature>() // Get only PlaceFeatures
            .map { it.placeId } // Extract their place IDs

        var changed = false
        clickedPlaceIds.forEach { placeId ->
            if (selectedPlaceIds.contains(placeId)) {
                selectedPlaceIds.remove(placeId)
                changed = true
            } else {
                selectedPlaceIds.add(placeId)
                changed = true
            }
        }

        // If the selection changed and the country layer is enabled, re-apply its style
        if (changed && countryEnabled) {
            Log.d(TAG, "Country selection changed. Selected IDs: ${"$"}selectedPlaceIds")
            countryLayer?.featureStyle = countryStyleFactory // Re-apply the factory
        } else if (!countryEnabled) {
            Log.d(TAG, "Country clicked but layer not enabled.")
            // Optional: Show a toast? "Enable country layer to select"
        }
    }


    /**
     * Handles clicks on the PopupMenu items.
     */
    override fun onMenuItemClick(item: MenuItem): Boolean {
        val id = item.itemId
        item.isChecked = !item.isChecked // Toggle the checkmark

        when (id) {
            R.id.boundary_type_locality -> {
                localityEnabled = item.isChecked
            }
            R.id.boundary_type_administrative_area_level_1 -> {
                adminAreaEnabled = item.isChecked
            }
            R.id.boundary_type_country -> {
                countryEnabled = item.isChecked
                // If disabling country layer, clear selection visually (optional)
                // if (!countryEnabled) selectedPlaceIds.clear()
            }
            else -> return false // Unknown item
        }

        updateStyles() // Apply changes to map layers
        return true
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "data_driven_boundaries",
    title = "Data-Driven Boundaries",
    description = "Dynamic styling and click handlers for administrative boundaries (Localities, States, Countries).",
    category = "Data-Driven Styling",
    complexity = Complexity.ADVANCED,
    tags = {"#boundaries", "#datadriven", "#featurelayer", "#locality", "#choropleth"},
    purpose = "Demonstrates styling administrative boundaries dynamically via FeatureLayer and capturing boundary clicks.",
    successCriteria = "Boundaries render with custom stroke and fill colors; tapping a region highlights its polygon.",
    failureIndicators = "Boundary layer is null (requires vector map / Map ID) or click listener not firing.",
    framework = Framework.JAVA_VIEWS
)
public class DataDrivenBoundariesActivity extends SamplesBaseActivity implements OnMapReadyCallback,
        FeatureLayer.OnFeatureClickListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = DataDrivenBoundariesActivity.class.getName();

    private static final LatLng HANA_HAWAII = new LatLng(20.7522, -155.9877); // Hana, Hawaii
    private static final LatLng CENTER_US = new LatLng(39.8283, -98.5795); // Approximate geographical center of the contiguous US

    private GoogleMap map;

    private FeatureLayer localityLayer = null;
    private FeatureLayer areaLevel1Layer = null;
    private FeatureLayer countryLayer = null;

    private final FeatureLayer.StyleFactory localityStyleFactory = getLocalityStyleFactory();
    private final FeatureLayer.StyleFactory countryStyleFactory = getCountryStyleFactory();
    private final FeatureLayer.StyleFactory areaLevel1StyleFactory = getAreaLevel1StyleFactory();

    // Which layers are currently enabled
    private boolean localityEnabled = true;
    private boolean adminAreaEnabled = false;
    private boolean countryEnabled = false;

    private final Set<String> selectedPlaceIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.data_driven_boundaries_demo);

        

        // --- Programmatically Create and Add Map Fragment ---
        // 1. Create GoogleMapOptions
        GoogleMapOptions mapOptions = new GoogleMapOptions();

        // 2. Set the mapId from the secrets.properties file
        mapOptions.mapId(mapId);
        // 3. Create SupportMapFragment instance with options
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);

        // 4. Add the fragment to your FrameLayout container using FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_container, mapFragment); // Use the container ID from XML
        fragmentTransaction.commit();
        // --- End Programmatic Creation ---

        mapFragment.getMapAsync(this);

        findViewById(R.id.button_hawaii).setOnClickListener(view -> centerMapOnLocation(HANA_HAWAII, 11f));
        findViewById(R.id.button_us).setOnClickListener(view -> centerMapOnLocation(CENTER_US, 1f));

        applyInsets(findViewById(R.id.map_container));

        setupBoundarySelectorButton();

        
    }

    private void setupBoundarySelectorButton() {
        MaterialButton stylingTypeButton = findViewById(R.id.button_feature_type);
        stylingTypeButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(this, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.boundary_types_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(this);

                popupMenu.getMenu().findItem(R.id.boundary_type_locality).setChecked(localityEnabled);
                popupMenu.getMenu().findItem(R.id.boundary_type_administrative_area_level_1).setChecked(adminAreaEnabled);
                popupMenu.getMenu().findItem(R.id.boundary_type_country).setChecked(countryEnabled);
                popupMenu.show();
        });
    }
    // [END_EXCLUDE]

    private void centerMapOnLocation(LatLng location, float zoomLevel) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;
        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());

        if (!capabilities.isDataDrivenStylingAvailable()) {
            Toast.makeText(
                    this,
                    "Data-driven Styling is not available.  See README.md for instructions.",
                    Toast.LENGTH_LONG
            ).show();
        }

        // Gets the LOCALITY feature layer.
        localityLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.LOCALITY)
                        .build()
        );

        // Gets the ADMINISTRATIVE_AREA_LEVEL_1 feature layer.
        areaLevel1Layer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.ADMINISTRATIVE_AREA_LEVEL_1)
                        .build()
        );

        // Gets the COUNTRY feature layer.
        countryLayer = googleMap.getFeatureLayer(
                new FeatureLayerOptions.Builder()
                        .featureType(FeatureType.COUNTRY)
                        .build()
        );
        countryLayer.addOnFeatureClickListener(this);

        centerMapOnLocation(HANA_HAWAII, 11f);

        // Apply the current set of styles.
        updateStyles();
    }

    /**
     * Updates the styles of the locality, area level 1, and country layers based on the current
     * state of the `localityEnabled`, `adminAreaEnabled`, and `countryEnabled` flags.
     * <p>
     * For each layer, if the corresponding flag is true, the layer's features will be styled using
     * the layer specific style factory function.
     */
    private void updateStyles() {
        if (localityLayer != null && areaLevel1Layer != null && countryLayer != null) {
            localityLayer.setFeatureStyle(localityEnabled ? localityStyleFactory : null);
            areaLevel1Layer.setFeatureStyle(adminAreaEnabled ? areaLevel1StyleFactory : null);
            if (countryEnabled) {
                countryLayer.setFeatureStyle(countryStyleFactory);
            } else {
                countryLayer.setFeatureStyle(null);
            }
        }
    }

    /**
     * Creates a StyleFactory for a FeatureLayer that styles Hana, HI on its Place ID.
     * <p>
     * This method defines a style factory that checks if a given feature is a {@link PlaceFeature}.
     * and if that feature matches "ChIJ0zQtYiWsVHkRk8lRoB1RNPo" (Hana, HI) applies a specific style.
     * Otherwise, it returns null, indicating no specific styling is applied.
     *
     * @return A {@link FeatureLayer.StyleFactory} instance that can be used to style features in a FeatureLayer.
     *         The factory returns a {@link FeatureStyle} for Hana, HI, and null for other features.
     */
    private static FeatureLayer.StyleFactory getLocalityStyleFactory() {
        int purple = 0x810FCB;
        // Define a style with purple fill at 50% opacity and
        // solid purple border.
        int fillColor = setAlphaValueOnColor(purple, 0.5f);
        int strokeColor = setAlphaValueOnColor(purple, 1f);

        return feature -> {
            // Check if the feature is an instance of PlaceFeature,
            // which contains a place ID.
            if (feature instanceof PlaceFeature placeFeature) {

                // Determine if the place ID is for Hana, HI.
                if ("ChIJ0zQtYiWsVHkRk8lRoB1RNPo".equals(placeFeature.getPlaceId())) {
                    // Use FeatureStyle.Builder to configure the FeatureStyle object
                    // returned by the style factory function.
                    return new FeatureStyle.Builder()
                            .fillColor(fillColor)
                            .strokeColor(strokeColor)
                            .build();
                }
            }
            return null;
        };
    }

    /**
     * Creates a StyleFactory for area level 1 features (e.g., states, provinces).
     * <p>
     * This factory provides a semi-transparent fill color for each area level 1 feature.
     * <p>
     * @return A StyleFactory that can be used to style area level 1 features on a map.
     */
    private static FeatureLayer.StyleFactory getAreaLevel1StyleFactory() {
        int alpha = (int) (255 * 0.25);

        return feature -> {
            if (feature instanceof PlaceFeature placeFeature) {

                // Return a hueColor in the range [-299,299]. If the value is
                // negative, add 300 to make the value positive.
                int hueColor = placeFeature.getPlaceId().hashCode() % 300;
                if (hueColor < 0) {
                    hueColor += 300;
                }
                return new FeatureStyle.Builder()
                        // Set the fill color for the state based on the hashed hue color.
                        .fillColor(Color.HSVToColor(alpha, new float[]{hueColor, 1f, 1f}))
                        .build();
            }
            return null;
        };
    }

    /**
     * Creates a StyleFactory for styling country features on a FeatureLayer highlighting selected
     * countries. Selection is determined via the selectedPlaceIds set.
     * <p>
     * *Note:* If the set of selected countries changes, this function must be called to update the
     * styling.
     * <p>
     * @return A FeatureLayer.StyleFactory that can be used to style country features.
     */
    private FeatureLayer.StyleFactory getCountryStyleFactory() {
        int defaultFillColor = setAlphaValueOnColor(Color.BLACK, 0.1f);
        int selectedFillColor = setAlphaValueOnColor(Color.RED, 0.33f);
        return feature -> {
            if (feature instanceof PlaceFeature) {
                int fillColor = selectedPlaceIds.contains(((PlaceFeature) feature).getPlaceId()) ? selectedFillColor : defaultFillColor;
                FeatureStyle.Builder build = new FeatureStyle.Builder();
                return build.fillColor(fillColor).strokeColor(Color.BLACK).build();
            }
            return null;
        };
    }

    /**
     * Called when a feature is clicked on the map.  It is only applied to the country layer.
     * <p>
     * Each time a country is clicked, its place ID is added to the selectedPlaceIds set or removed
     * if it was already present.  Each time the set is
     * <p>
     */
    @Override
    public void onFeatureClick(@NonNull FeatureClickEvent event) {
        // Get the list of features affected by the click using
        // getPlaceIds() defined below.
        List<String> newSelectedPlaceIds = getPlaceIds(event.getFeatures());

        for (String placeId : newSelectedPlaceIds) {
            if (selectedPlaceIds.contains(placeId)) {
                selectedPlaceIds.remove(placeId);
            } else {
                selectedPlaceIds.add(placeId);
            }
        }

        // Reset the feature styling
        countryLayer.setFeatureStyle(countryStyleFactory);
    }

    // Gets a List of place IDs from the FeatureClickEvent object.
    private List<String> getPlaceIds(List<Feature> features) {
        List<String> placeIds = new ArrayList<>();
        for (Feature feature : features) {
            if (feature instanceof PlaceFeature) {
                placeIds.add(((PlaceFeature) feature).getPlaceId());
            }
        }
        return placeIds;
    }

    private static int setAlphaValueOnColor(int color, float alpha) {
        return (color & 0x00ffffff) | (round(alpha * 255) << 24);
    }

    /**
     * Handles the click events for menu items in the boundary type selection menu.
     * This method is called when a user selects a boundary type (locality, administrative area, or country) from the menu.
     * It toggles the checked state of the selected menu item and updates the corresponding boolean flags (localityEnabled, adminAreaEnabled, countryEnabled).
     * Finally, it calls the {@link #updateStyles()} method to reflect the changes in the map's display.
     *
     * @param item The MenuItem that was clicked.
     * @return True if the event was handled, false otherwise. In this case it always return true if one of the correct items was selected.
     */ 
}
""".trimIndent()
        ),

        "com.example.kotlindemos.DataDrivenDatasetStylingActivity" to SnippetPair(
            regionTag = "maps_android_data_driven_styling_datasets",
            kotlinCode = """
@Sample(
    id = "data_driven_datasets",
    title = "Data-Driven Dataset Styling",
    description = "Styling custom geospatial datasets uploaded to Google Cloud Platform based on attributes.",
    category = "Data-Driven Styling",
    complexity = Complexity.ADVANCED,
    tags = ["#datasets", "#datadriven", "#clouddata", "#attributes", "#filtering"],
    purpose = "Demonstrates loading a Cloud Dataset FeatureLayer and applying dynamic style rules based on feature properties.",
    successCriteria = "Dataset points and polygons display distinct styling according to attribute values.",
    failureIndicators = "Dataset ID invalid or attributes fail to filter correctly.",
    framework = Framework.KOTLIN_VIEWS
)
class DataDrivenDatasetStylingActivity : SamplesBaseActivity(), OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {
    private lateinit var mapContainer: ViewGroup

    private lateinit var map: GoogleMap

    private var datasetLayer: FeatureLayer? = null

    // The global id of the clicked dataset feature.
    private var lastGlobalId: String? = null

    private data class DataSet(
        val datasetId: String,
        val bounds: LatLngBounds,
        val callback: DataDrivenDatasetStylingActivity.() -> Unit
    )

    private val dataSets = mutableMapOf<String, DataSet>()

    private lateinit var buttonLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val mapId = (application as ApiDemoApplication).mapId

        // --- Map ID Check ---
        if (mapId == null) {
            finish()
            return // Exit early if no valid Map ID
        }

        if (dataSets.isEmpty()) {
            with(dataSets) {
                put(
                    getString(com.example.common_ui.R.string.boulder),
                    DataSet(
                        BuildConfig.BOULDER_DATASET_ID,
                        LatLngBounds(LatLng(39.920, -105.340), LatLng(40.090, -105.210))
                    ) { styleBoulderDataset() }
                )
                put(
                    getString(com.example.common_ui.R.string.new_york),
                    DataSet(
                        BuildConfig.NEW_YORK_DATASET_ID,
                        LatLngBounds(LatLng(40.7640, -73.9820), LatLng(40.8000, -73.9490))
                    ) { styleNYCDataset() }
                )
                put(
                    getString(com.example.common_ui.R.string.kyoto),
                    DataSet(
                        BuildConfig.KYOTO_DATASET_ID,
                        LatLngBounds(LatLng(34.9700, 135.7200), LatLng(35.0400, 135.8000))
                    ) { styleKyotoDataset() }
                )
            }
        }

        setContentView(com.example.common_ui.R.layout.data_driven_styling_demo)

        mapContainer = findViewById(com.example.common_ui.R.id.map_container)

        // --- Programmatically create and add the map fragment ---
        // 1. Create GoogleMapOptions
        val mapOptions = GoogleMapOptions().apply {
            // 2. Set the mapId using your BuildConfig field
            mapId(mapId)
        }

        // 3. Create SupportMapFragment instance with options
        val mapFragment = SupportMapFragment.newInstance(mapOptions)

        // 4. Add the fragment to your FrameLayout container
        supportFragmentManager.beginTransaction()
            .replace(com.example.common_ui.R.id.map_fragment_container, mapFragment) // Use the container ID from XML
            .commit()
        // --- End of programmatic creation ---

        mapFragment.getMapAsync(this)

        // Set the click listener for each of the buttons
        listOf(com.example.common_ui.R.id.button_kyoto, com.example.common_ui.R.id.button_ny, com.example.common_ui.R.id.button_boulder).forEach { viewId ->
            findViewById<Button>(viewId).setOnClickListener { view ->
                switchToDataset((view as Button).text.toString())
            }
        }

        buttonLayout = findViewById<View>(com.example.common_ui.R.id.button_kyoto).parent as LinearLayout

        handleCutout()
        applyInsets(findViewById(com.example.common_ui.R.id.map_container))
    }

    private fun handleCutout() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
                val topInset = insets.top
                mapContainer.setPadding(0, topInset, 0, 0)
                windowInsets
            }
        } else {
            window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
                @Suppress("DEPRECATION")
                val topInset = windowInsets.systemWindowInsetTop
                mapContainer.setPadding(0, topInset, 0, 0)
                windowInsets
            }
        }
    }

    /**
     * Switches the currently displayed dataset on the map to the one identified by the given label.
     *
     * This function retrieves a DataSetInfo object from the `dataSets` map using the provided `label`.
     * If a dataset with the given label exists, it does the following:
     *   1. Creates a new FeatureLayer for the specified dataset.
     *   2. Sets the `datasetLayer` property to the newly created FeatureLayer.
     *   3. Executes the callback function associated with the dataset, passing the current activity instance (this).
     *   4. Centers the map on the location associated with the dataset.
     * If no dataset with the given label is found, it displays a Toast message indicating an unknown dataset.
     *
     * @param label The label identifying the dataset to switch to. This label should correspond to a key in the `dataSets` map.
     * @throws IllegalStateException if `map` is not initialized.
     */
    private fun switchToDataset(label: String) {
        dataSets[label]?.let { dataSet ->
            datasetLayer = map.getFeatureLayer(
                with(FeatureLayerOptions.Builder()) {
                    featureType(FeatureType.DATASET)
                    // Specify the dataset ID.
                    datasetId(dataSet.datasetId)
                }.build()
            )
            dataSet.callback(this)
            centerMapOnBounds(dataSet.bounds)
        } ?: run {
            Toast.makeText(this, "Unknown dataset: ${"$"}label", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        googleMap.setOnCameraIdleListener {
            val cp = googleMap.cameraPosition
            Log.i(TAG, "CAMERA_PARAMS: target=LatLng(${"$"}{cp.target.latitude}, ${"$"}{cp.target.longitude}), zoom=${"$"}{cp.zoom}f, tilt=${"$"}{cp.tilt}f, bearing=${"$"}{cp.bearing}f")
        }

        val capabilities: MapCapabilities = map.mapCapabilities
        println("Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable)

        switchToDataset("Boulder")

        // Register the click event handler for the Datasets layer.
        datasetLayer?.addOnFeatureClickListener(this)
    }

    private fun styleNYCDataset() {
        data class Style(
            @ColorInt val fillColor: Int,
            @ColorInt val strokeColor: Int,
            val pointRadius: Float,
        )

        val largePointRadius = 8F
        val smallPointRadius = 6F
        val darkRedBrown = ContextCompat.getColor(this, R.color.darkRedBrown)

        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->
            if (feature is DatasetFeature) {
                val furColors: MutableMap<String, String> = feature.datasetAttributes
                val furColor = furColors["Color"]

                val style = when (furColor) {
                    "Black+" -> Style(Color.BLACK, Color.BLACK, largePointRadius)
                    "Cinnamon+" -> Style(darkRedBrown, darkRedBrown, largePointRadius)
                    "Cinnamon+Gray" -> Style(darkRedBrown, darkRedBrown, smallPointRadius)
                    "Cinnamon+White" -> Style(darkRedBrown, Color.WHITE, smallPointRadius)
                    "Gray+" -> Style(Color.GRAY, Color.YELLOW, largePointRadius) // Default stroke
                    "Gray+Cinnamon" -> Style(Color.GRAY, darkRedBrown, smallPointRadius)
                    "Gray+Cinnamon, White" -> Style(Color.LTGRAY, darkRedBrown, smallPointRadius)
                    "Gray+White" -> Style(Color.GRAY, Color.WHITE, smallPointRadius)
                    else -> Style(Color.GREEN, Color.YELLOW, largePointRadius) // Default style if furColor is null or doesn't match
                }

                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(style.fillColor)
                    .strokeColor(style.strokeColor)
                    .pointRadius(style.pointRadius)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }

    private fun styleKyotoDataset() {

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {
                // Determine the value of the typecategory attribute.
                val typeCategories: MutableMap<String, String> = feature.datasetAttributes
                val typeCategory = typeCategories["type"]
                // Set default colors to green.
                val fillColor: Int
                val strokeColor: Int
                when (typeCategory) {
                    "temple" -> {
                        // Color temples areas blue.
                        fillColor = Color.BLUE
                        strokeColor = Color.BLUE
                    }

                    else -> {
                        // Color all other areas green.
                        fillColor = Color.GREEN
                        strokeColor = Color.GREEN
                    }
                }
                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .strokeWidth(2F)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }

    private fun styleBoulderDataset() {
        val EASY = Color.GREEN
        val MODERATE = Color.BLUE
        val DIFFICULT = Color.RED

        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Set default colors to to yellow and point radius to 8.
            var fillColor: Int
            var strokeColor: Int
            val pointRadius = 8F
            var strokeWidth = 3F
            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {

                val attributes: MutableMap<String, String> = feature.datasetAttributes
                val difficulty = attributes["OSMPTrailsOSMPDIFFICULTY"]
                val name = attributes["OSMPTrailsOSMPTRAILNAME"]
                val dogsAllowed = attributes["OSMPTrailsOSMPDOGREGGEN"]

                when (difficulty) {
                    "Easy" -> {
                        fillColor = EASY
                    }
                    "Moderate" -> {
                        fillColor = MODERATE
                    }
                    "Difficult" -> {
                        fillColor = DIFFICULT
                    }
                    else -> {
                        Log.w(TAG, "${"$"}name -> Unknown difficulty: ${"$"}difficulty")
                        fillColor = Color.MAGENTA
                    }
                }

                when (dogsAllowed) {
                    "No Dogs" -> {
                        fillColor = ColorUtils.setAlphaComponent(fillColor, 66)
                        strokeWidth = 5f
                    }
                    "LVS" -> {
                    }
                    "LR", "RV" -> {
                        fillColor = ColorUtils.setAlphaComponent(fillColor, 75)
                    }
                    else -> {
                        Log.w(TAG, "${"$"}name -> Unknown dogs reg: ${"$"}dogsAllowed")
                    }
                }

                strokeColor = fillColor

                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .pointRadius(pointRadius)
                    .strokeWidth(strokeWidth)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the feature layer.
        datasetLayer?.featureStyle = styleFactory
    }


    private fun centerMapOnBounds(bounds: LatLngBounds, padding: Int = 80) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }

    // Define the click event handler to set lastGlobalId to globalid of selected feature.
    override fun onFeatureClick(event: FeatureClickEvent) {
        // Get the dataset feature affected by the click.
        val clickFeatures: MutableList<Feature> = event.features
        lastGlobalId = null
        if (clickFeatures[0] is DatasetFeature) {
            lastGlobalId = ((clickFeatures[0] as DatasetFeature).datasetAttributes["globalid"])
            // Remember to reset the Style Factory.
            styleDatasetsLayerClickEvent()
        }
    }

    // Set fill and border for all features.
    private fun styleDatasetsLayerClickEvent() {
        // Create the style factory function.
        val styleFactory = FeatureLayer.StyleFactory { feature: Feature ->

            // Check if the feature is an instance of DatasetFeature.
            if (feature is DatasetFeature) {
                val globalIDs: MutableMap<String, String> = feature.datasetAttributes
                // Determine globalid attribute.
                val globalID = globalIDs["globalid"]
                // Set default colors to to green.
                var fillColor = Color.GREEN
                var strokeColor = Color.GREEN
                if (globalID == lastGlobalId) {
                    // Color selected area blue.
                    fillColor = Color.BLUE
                    strokeColor = Color.BLUE
                }
                return@StyleFactory FeatureStyle.Builder()
                    .fillColor(fillColor)
                    .strokeColor(strokeColor)
                    .build()
            }
            return@StyleFactory null
        }

        // Apply the style factory function to the dataset feature layer.
        datasetLayer?.featureStyle = styleFactory
    }
}
""".trimIndent(),
            javaCode = """
@Sample(
    id = "data_driven_datasets",
    title = "Data-Driven Dataset Styling",
    description = "Styling custom geospatial datasets uploaded to Google Cloud Platform based on attributes.",
    category = "Data-Driven Styling",
    complexity = Complexity.ADVANCED,
    tags = {"#datasets", "#datadriven", "#clouddata", "#attributes", "#filtering"},
    purpose = "Demonstrates loading a Cloud Dataset FeatureLayer and applying dynamic style rules based on feature properties.",
    successCriteria = "Dataset points and polygons display distinct styling according to attribute values.",
    failureIndicators = "Dataset ID invalid or attributes fail to filter correctly.",
    framework = Framework.JAVA_VIEWS
)
public class DataDrivenDatasetStylingActivity extends SamplesBaseActivity implements OnMapReadyCallback, FeatureLayer.OnFeatureClickListener {
    private record DataSet(
            String label,
            String datasetId,
            LatLngBounds bounds,
            DataDrivenDatasetStylingActivity.DataSet.StylingCallback callback) {
            public interface StylingCallback {
                void styleDatasetLayer();
            }
    }

    /**
     * An array of DataSet objects representing different geographic locations and their associated data.
     * Each DataSet contains:
     *  - A human-readable name (e.g., "Boulder", "New York").
     *  - A unique Dataset ID, which should correspond to a dataset id in the Datasets console tab.
     *  - The LatLngBounds of the dataset area.
     *  - A styling function (method reference) that defines how to style the data from that dataset on a map.
     */
    private final DataSet[] dataSets = new DataSet[] {
            new DataSet("Boulder", BuildConfig.BOULDER_DATASET_ID,
                    new LatLngBounds(new LatLng(39.920, -105.340), new LatLng(40.090, -105.210)),
                    this::styleBoulderDatasetLayer),
            new DataSet("New York", BuildConfig.NEW_YORK_DATASET_ID,
                    new LatLngBounds(new LatLng(40.7640, -73.9820), new LatLng(40.8000, -73.9490)),
                    this::styleNYCDatasetLayer),
            new DataSet("Kyoto", BuildConfig.KYOTO_DATASET_ID,
                    new LatLngBounds(new LatLng(34.9700, 135.7200), new LatLng(35.0400, 135.8000)),
                    this::styleKyotoDatasetsLayer),
    };

    private DataSet findDataSetByLabel(String label) {
        for (DataSet dataSet : dataSets) {
            if (dataSet.label().equalsIgnoreCase(label)) { // Case-insensitive comparison
                return dataSet;
            }
        }
        return null; // Return null if no match is found
    }

    private static final String TAG = DataDrivenDatasetStylingActivity.class.getName();
    private static FeatureLayer datasetLayer = null;
    private GoogleMap map;
    private String lastGlobalId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_driven_styling_demo);

        

        // --- Programmatically Create and Add Map Fragment ---
        // 1. Create GoogleMapOptions
        GoogleMapOptions mapOptions = new GoogleMapOptions();

        // 2. Set the mapId from the secrets.properties file
        mapOptions.mapId(BuildConfig.MAP_ID); // Use the mapId retrieved earlier

        // 3. Create SupportMapFragment instance with options
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(mapOptions);
        mapFragment.getMapAsync(this);

        // 4. Add the fragment to your FrameLayout container using FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map_fragment_container, mapFragment); // Use the container ID from XML
        fragmentTransaction.commit();
        // --- End Programmatic Creation ---


        int[] buttonIds = {R.id.button_boulder, R.id.button_ny, R.id.button_kyoto};
        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(view -> switchDataSet(((Button) view).getText().toString()));
        }

        applyInsets(findViewById(R.id.map_container));
    }

    /**
     * Switches the currently displayed dataset to the one specified by the provided label.
     * <p>
     * This method attempts to find a DataSet object associated with the given label.
     * If a matching DataSet is found, it updates the map's feature layer to display the
     * data from that dataset. It also applies styling to the new dataset layer and centers
     * the map on the dataset's specified location.
     * <p>
     * If no matching DataSet is found, a toast message is displayed indicating the failure.
     * <p>
     * @param label The label of the dataset to switch to. This label should correspond to a
     *              dataset that has been previously added or loaded.
     */
    private void switchDataSet(String label) {
        DataSet dataSet = findDataSetByLabel(label);
        if (dataSet == null) {
            Toast.makeText(this, "Failed to find dataset" + label, Toast.LENGTH_SHORT).show();
        } else {
            datasetLayer = map.getFeatureLayer(
                    new FeatureLayerOptions.Builder()
                            .featureType(FeatureType.DATASET)
                            .datasetId(dataSet.datasetId())
                            .build()
            );
            dataSet.callback.styleDatasetLayer();
            centerMapOnBounds(dataSet.bounds());
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;

        googleMap.setOnCameraIdleListener(() -> {
            com.google.android.gms.maps.model.CameraPosition cp = googleMap.getCameraPosition();
            Log.i(TAG, "CAMERA_PARAMS: target=LatLng(" + cp.target.latitude + ", " + cp.target.longitude + "), zoom=" + cp.zoom + "f, tilt=" + cp.tilt + "f, bearing=" + cp.bearing + "f");
        });

        MapCapabilities capabilities = map.getMapCapabilities();
        Log.d(TAG, "Data-driven Styling is available: " + capabilities.isDataDrivenStylingAvailable());
        if (!capabilities.isDataDrivenStylingAvailable()) {
            Toast.makeText(
                    this,
                    "Data-driven Styling is not available.  See README.md for instructions.",
                    Toast.LENGTH_LONG
            ).show();
        }

        // Switch to the default dataset which must happen before adding the feature click listener
        switchDataSet("Boulder");

        datasetLayer.addOnFeatureClickListener(this);
    }

    private void styleNYCDatasetLayer() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            int fillColor = Color.GREEN;
            int strokeColor = Color.YELLOW;
            float pointRadius = 12F;

            if (feature instanceof DatasetFeature) {
                Map<String, String> furColors = ((DatasetFeature) feature).getDatasetAttributes();
                String furColor = furColors.get("Color");

                if (furColor != null) {
                    switch (furColor) {
                        case "Black+":
                            fillColor = Color.BLACK;
                            strokeColor = Color.BLACK;
                            break;
                        case "Cinnamon+":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = 0xFF8B0000;
                            break;
                        case "Cinnamon+Gray":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = 0xFF8B0000;
                            pointRadius = 10F;
                            break;
                        case "Cinnamon+White":
                            fillColor = 0xFF8B0000; // dark red color
                            strokeColor = Color.WHITE;
                            pointRadius = 10F;
                            break;
                        case "Gray+":
                            fillColor = Color.GRAY;
                            break;
                        case "Gray+Cinnamon":
                            fillColor = Color.GRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 10F;
                            break;
                        case "Gray+Cinnamon, White":
                            fillColor = Color.LTGRAY;
                            strokeColor = 0xFF8B0000; // dark red color
                            pointRadius = 10F;
                            break;
                        case "Gray+White":
                            fillColor = Color.GRAY;
                            strokeColor = Color.WHITE;
                            pointRadius = 10F;
                            break;
                    }
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .pointRadius(pointRadius)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void styleKyotoDatasetsLayer() {
        // Create the style factory function.
        FeatureLayer.StyleFactory styleFactory = feature -> {
            // Check if the feature is an instance of DatasetFeature.
            if (feature instanceof DatasetFeature datasetFeature) {
                // Determine the value of the typecategory attribute.
                Map<String, String> typeCategories = datasetFeature.getDatasetAttributes();
                String typeCategory = typeCategories.get("type");

                // Set default colors to green.
                int fillColor;
                int strokeColor;

                if ("temple".equals(typeCategory)) {
                    // Color temples areas blue.
                    fillColor = Color.BLUE;
                    strokeColor = Color.BLUE;
                } else {
                    // Color all other areas green.
                    fillColor = Color.GREEN;
                    strokeColor = Color.GREEN;
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .strokeWidth(2F)
                        .build();
            }
            return null;
        };

        // Apply the style factory function to the feature layer.
        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }

    private void styleBoulderDatasetLayer() {
        final int EASY = Color.GREEN;
        final int MODERATE = Color.BLUE;
        final int DIFFICULT = Color.RED;

        // Create the style factory function.
        FeatureLayer.StyleFactory styleFactory = feature -> {
            // Set default colors to yellow and point radius to 8.
            int fillColor;
            int strokeColor;
            float pointRadius = 8F;
            float strokeWidth = 3F;

            // Check if the feature is an instance of DatasetFeature.
            if (feature instanceof DatasetFeature datasetFeature) {
                Map<String, String> attributes = datasetFeature.getDatasetAttributes();
                String difficulty = attributes.get("OSMPTrailsOSMPDIFFICULTY");
                String name = attributes.get("OSMPTrailsOSMPTRAILNAME");
                String dogsAllowed = attributes.get("OSMPTrailsOSMPDOGREGGEN");

                if ("Easy".equals(difficulty)) {
                    fillColor = EASY;
                } else if ("Moderate".equals(difficulty)) {
                    fillColor = MODERATE;
                } else if ("Difficult".equals(difficulty)) {
                    fillColor = DIFFICULT;
                } else {
                    Log.w(TAG, name + " -> Unknown difficulty: " + difficulty);
                    fillColor = Color.MAGENTA;
                }

                if ("No Dogs".equals(dogsAllowed)) {
                    fillColor = ColorUtils.setAlphaComponent(fillColor, 66);
                    strokeWidth = 5f;
                } else if ("LVS".equals(dogsAllowed)) {
                    // No change needed
                } else if ("LR".equals(dogsAllowed) || "RV".equals(dogsAllowed)) {
                    fillColor = ColorUtils.setAlphaComponent(fillColor, 75);
                } else {
                    Log.w(TAG, name + " -> Unknown dogs reg: " + dogsAllowed);
                }

                strokeColor = fillColor;

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .pointRadius(pointRadius)
                        .strokeWidth(strokeWidth)
                        .build();
            }
            return null;
        };

        // Apply the style factory function to the feature layer.
        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }


    private void centerMapOnBounds(LatLngBounds bounds) {
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
    }

    @Override
    public void onFeatureClick(FeatureClickEvent event) {
        List<Feature> clickFeatures = event.getFeatures();
        lastGlobalId = null;
        if (clickFeatures.get(0) instanceof DatasetFeature) {
            lastGlobalId = ((DatasetFeature) clickFeatures.get(0)).getDatasetAttributes().get("globalid");
            styleDatasetsLayerClickEvent();
        }
    }

    private void styleDatasetsLayerClickEvent() {
        FeatureLayer.StyleFactory styleFactory = feature -> {
            if (feature instanceof DatasetFeature) {
                Map<String, String> globalIDs = ((DatasetFeature) feature).getDatasetAttributes();
                String globalID = globalIDs.get("globalid");
                int fillColor = Color.GREEN;
                int strokeColor = Color.GREEN;

                if (globalID != null && globalID.equals(lastGlobalId)) {
                    fillColor = Color.BLUE;
                    strokeColor = Color.BLUE;
                }

                return new FeatureStyle.Builder()
                        .fillColor(fillColor)
                        .strokeColor(strokeColor)
                        .build();
            }
            return null;
        };

        if (datasetLayer != null) {
            datasetLayer.setFeatureStyle(styleFactory);
        }
    }
}
""".trimIndent()
        )
    )
}

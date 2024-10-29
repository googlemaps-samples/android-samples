{ pkgs, apikey, giturl, subdir, launchactivity,... }: {
  packages = [
      pkgs.git
      pkgs.sdkmanager
      pkgs.j2cli
  ];
  bootstrap = ''
    mkdir -p "$WS_NAME" tmp
    
    git clone --depth 1 ${giturl} tmp

    mv tmp/${subdir}/* "$WS_NAME"

    chmod -R +w "$WS_NAME"
    mkdir -p "$WS_NAME/.idx/"

    # Create a secrets.properties file in the repo
    touch $WS_NAME/secrets.properties

    # Create a secrets.properties variable for each key type in local.defaults.properties
    
    while IFS= read -r line || [[ -n "$line" ]]; do
      # Check that an "=" exists in the line
      if [[ $line == *"="* ]]; then
        # Extract the variable name
        keyVar=$(echo "$line" | cut -d '=' -f 1)
        # Define new variable in secrets file
        echo "$keyVar=\"${apikey}\"" >> $WS_NAME/secrets.properties
      fi
    done < $WS_NAME/local.defaults.properties

    # We create a dev.nix that builds the subproject specified at template instantiation
    launch_activity=${launchactivity} j2 --format=env ${./devNix.j2} -o $WS_NAME/.idx/dev.nix

    mv "$WS_NAME" "$out"
  '';
}

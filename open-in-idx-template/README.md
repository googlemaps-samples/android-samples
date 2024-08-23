## Open in Project IDX 

## How to use the template

This template allows opening Google Maps Platform Android tutorial code samples in IDX IDE. To open a code sample in IDX, go to `https://idx.google.com/new?template=https://github.com/googlemaps-samples/android-samples/open-in-idx-template` and provide the gitUrl, subdir and the name of the activity to launch as query params. 

```
https://idx.google.com/new
?template=https://github.com/googlemaps-samples/android-samples/open-in-idx-template
&giturl=https://github.com/googlemaps-samples/android-samples
&subdir=tutorials/java/CurrentPlaceDetailsOnMap/
&launchactivity=com.example.currentplacedetailsonmap/.MapsActivityCurrentPlace
&apikey=AIzaXXXXXXXXXXXXXXXX
```

Please review the template parameters in idx-template.json file. If a parameter is not provided in the link as a query param, the default value specified in the `idx-template.json` file will be used. If the parameter doesn't specify a default value and a value is not provided as a query param, the IDX workspace creation dialog asks for the value.

There is no default value for the API Key. You must provide it as a query parameter or alternatively in the IDX workspace creation dialog.

The example below demonstates how you could open a code sample in IDX. The link in the example will open the code sample specified by the default values in idx-template.json and you'd be asked to provide an API Key on IDX workspace creation dialog.

<a href="https://idx.google.com/new?template=https://github.com/googlemaps-samples/android-samples/open-in-idx-template&giturl=https://github.com/googlemaps-samples/android-samples&subdir=tutorials/java/CurrentPlaceDetailsOnMap/&launchactivity=com.example.currentplacedetailsonmap/.MapsActivityCurrentPlace">
  <img
    alt="Open in IDX"
    src="https://www.gstatic.com/monospace/230815/openinprojectidx.png"
    width="170"
  />
</a>
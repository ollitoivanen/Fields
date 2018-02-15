package com.fields.curiumx.fields;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ollitoivanen on 11.2.2018.
 */
//FoursquareResponse describes a response object from the Foursquare API.
public class FoursquareResponse {

    // A group object within the response.
    FoursquareGroup group;
    List<FoursquareVenue> venues = new ArrayList<>();
}

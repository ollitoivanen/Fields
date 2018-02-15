package com.fields.curiumx.fields;

/**
 * Created by ollitoivanen on 11.2.2018.
 */
//FoursquareVenue describes a venue object from the Foursquare API.
public class FoursquareVenue {
    // The ID of the venue.
    String id;

    // The name of the venue.
    String name;

    // The rating of the venue, if available.
    double rating;

    // A location object within the venue.
    FoursquareLocation location;
}

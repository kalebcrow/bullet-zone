package edu.unh.cs.cs619.bulletzone.rest;

import android.widget.WrapperListAdapter;

import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;

/** "http://stman1.cs.unh.edu:6191/games"
 * "http://10.0.0.145:6191/games"
 * http://10.0.2.2:8080/
 * Created by simon on 10/1/14.
 */

//Please fix when we submit
@Rest(rootUrl = "http://10.21.133.147:8080/games",
//@Rest(rootUrl = "http://10.21.138.125:8080/games",
//@Rest(rootUrl = "http://stman1.cs.unh.edu:6192/games",
//@Rest(rootUrl = "http://stman1.cs.unh.edu:61902/games",
//@Rest(rootUrl = "localhost:8080",
        converters = {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class}
        // TODO: disable intercepting and logging
        // , interceptors = { HttpLoggerInterceptor.class }
)
public interface BulletZoneRestClient extends RestClientErrorHandling {
    void setRootUrl(String rootUrl);

    @Post("/{userID}")
    LongArrayWrapper join(@Path long userID) throws RestClientException;

    @Get("")
    GridWrapper grid();

    @Get("/event/{timestamp}")
    EventWrapper event(@Path long timestamp);

    @Put("/account/register/{username}/{password}")
    BooleanWrapper register(@Path String username, @Path String password);

    @Put("/account/login/{username}/{password}")
    LongWrapper login(@Path String username, @Path String password);

    @Put("/account/{username}/balance")
    LongWrapper balance(@Path String username);

    @Put("/account/{username}/items")
    String items(@Path String username);

    @Put("/{tankId}/move/{direction}")
    BooleanWrapper move(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/turn/{direction}")
    BooleanWrapper turn(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/fire/1")
    BooleanWrapper fire(@Path long tankId);

    @Delete("/{tankId}/leave")
    BooleanWrapper leave(@Path long tankId);

    @Put("/{tankId}/moveTo/{desiredLocation}")
    BooleanWrapper moveTo(@Path long tankId, @Path int desiredLocation);

}

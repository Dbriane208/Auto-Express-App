package daniel.brian.autoexpress.payments.interceptor;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/*
- Interceptors are mechanisms of the OkHttp library that can add,remove, or replace request headers
- They can also transform the body of the requests that have one for example, you can use an application
an application interceptor to add request body compression if you're connecting to a webserver to
support it
*/
public class AuthInterceptor implements Interceptor {
    private String mAuthToken;

    public AuthInterceptor(String authToken){
        mAuthToken = authToken;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization","Bearer " + mAuthToken)
                .build();
        return chain.proceed(request);
    }
}

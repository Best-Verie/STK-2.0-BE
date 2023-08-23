package rw.gov.sacco.stockmis.v1.utils;

import org.springframework.http.ResponseEntity;
import rw.gov.sacco.stockmis.v1.payload.ApiResponse;

public class Formatter {

    public static ResponseEntity<ApiResponse> ok(Object body) {
        return ResponseEntity.ok(ApiResponse.success(body));
    }

    public static ResponseEntity<ApiResponse> ok(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    public static ResponseEntity<ApiResponse> done(){
        return ResponseEntity.ok(ApiResponse.success("Done"));
    }
    public static ResponseEntity<ApiResponse> failed(String message){
        return ResponseEntity.badRequest().body(ApiResponse.fail(message));
    }

}

package com.doghotel.reservation.global.util;

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public class ApiDocumentUtils {
    public static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(
                modifyUris() // (1)
                        .scheme("https")
                        .host("localhost")
                        .removePort(),
                prettyPrint()); // (2)
    }

    public static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint()); // (3)
    }
}

package com.foober.foober.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ImageResponse {
    private String id;
    private String name;
    private Long size;
    private String url;
    private String contentType;
}

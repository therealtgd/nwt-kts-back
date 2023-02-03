package com.foober.foober.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Image")
public class Image {
    @Id
    @SequenceGenerator(name = "mySeqGenV1", sequenceName = "mySeqV1", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mySeqGenV1")
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    private String contentType;

    @Column(name = "size", nullable = false)
    private Long size;
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name = "data", nullable = false)
    private byte[] data;

    public Image (String filename,
                  String contentType,
                  Long size,
                  byte[] data) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.data = data;
    }

}

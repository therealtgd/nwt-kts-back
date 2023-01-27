package com.foober.foober.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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

    @Column(name = "filename", nullable = false, unique = true)
    private String filename;

    private String contentType;

    @Column(name = "size", nullable = false)
    private Long size;
    @Lob
    @Column(name = "data", nullable = false)
    private byte[] data;

}

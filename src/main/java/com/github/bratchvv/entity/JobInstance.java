package com.github.bratchvv.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Vladimir Bratchikov
 */
@Data
@NoArgsConstructor
@Document(collection = "job_instances")
public class JobInstance implements Serializable {

    @Id
    private ObjectId id;
    private Status status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String jobKey;
}

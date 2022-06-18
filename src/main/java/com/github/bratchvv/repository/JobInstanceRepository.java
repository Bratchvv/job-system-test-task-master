package com.github.bratchvv.repository;

import com.github.bratchvv.entity.JobInstance;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Vladimir Bratchikov
 */
@Repository
public interface JobInstanceRepository extends MongoRepository<JobInstance, ObjectId> {

}

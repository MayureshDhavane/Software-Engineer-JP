package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRecordRepository extends CrudRepository<UserRecord, Long> {
//    UserRecord findById(long id);
    Optional<UserRecord> findByName(String name);
}

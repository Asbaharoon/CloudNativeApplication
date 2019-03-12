package com.web.cloudapp.Repository;

import com.web.cloudapp.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
}

// UserRepository
package com.example.shoppingmall.repository;
import com.example.shoppingmall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
package com.ghraphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ghraphql.model.Student;
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
Student findByAddress(String address);
}

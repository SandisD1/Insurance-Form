package com.example.application.data.repository;

import com.example.application.data.entity.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManufacturersRepository extends JpaRepository<Manufacturer, Integer> {

}

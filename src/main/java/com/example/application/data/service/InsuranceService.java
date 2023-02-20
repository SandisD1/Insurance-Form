package com.example.application.data.service;

import com.example.application.data.entity.Car;
import com.example.application.data.entity.Manufacturer;
import com.example.application.data.entity.Person;
import com.example.application.data.repository.CarRepository;
import com.example.application.data.repository.ManufacturersRepository;
import com.example.application.data.repository.PersonRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class InsuranceService {
    private final ManufacturersRepository manufacturersRepository;

    private final CarRepository carRepository;

    private final PersonRepository personRepository;

    public InsuranceService(ManufacturersRepository manufacturersRepository, CarRepository carRepository, PersonRepository personRepository) {
        this.manufacturersRepository = manufacturersRepository;
        this.carRepository = carRepository;
        this.personRepository = personRepository;
        this.parseManufacturers();
    }

    private void parseManufacturers() {
        List<Manufacturer> manufacturers = new ArrayList<>();
        try {
            File file = new File("src/main/resources/data/manufacturers.xlsx");
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() < 6) {
                    continue;
                }
                String manufacturer = row.getCell(0).toString();
                if (manufacturer.equals("PĀRĒJĀS MARKAS")) {
                    break;
                }
                Manufacturer next = new Manufacturer();
                next.setManufacturerName(manufacturer);
                manufacturers.add(next);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.manufacturersRepository.saveAll(manufacturers);
    }

    public List<Manufacturer> getManufacturers() {
        return this.manufacturersRepository.findAll();
    }

    public Person savePerson(Person person) {
        return this.personRepository.save(person);
    }

    public Car saveCar(Car car) {
        return this.carRepository.save(car);
    }
}




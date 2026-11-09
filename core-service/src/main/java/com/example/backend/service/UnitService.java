package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.dto.request.NewUnitRequest;
import com.example.backend.entity.Unit;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.repository.UnitRepository;

@Service 
public class UnitService {
    private final UnitRepository unitRepository;

    UnitService(UnitRepository unitRepository){
        this.unitRepository = unitRepository;
    }

    public Unit newUnit(NewUnitRequest request){
        Unit newUnit = new Unit(request.getCode(), request.getName());
        unitRepository.save(newUnit);
        return newUnit;
    }

    public List<Unit> getAllUnits(){
        List<Unit> units = unitRepository.findAll();
        return units;
    }

    public Unit getUnit(String code){
        Optional<Unit> optionalUnit = unitRepository.findById(code);
        Unit unit = optionalUnit.orElseThrow(()->{
            throw new BusinessException(BusinessError.UNIT_NOT_FOUND, new Object[]{code});
        });
        return unit;
    }

    public void deleteUnit(String code){
        unitRepository.deleteById(code);
    }
}

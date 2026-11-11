package com.example.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.dto.request.NewMajorRequest;
import com.example.backend.dto.request.PatchMajorRequest;
import com.example.backend.entity.Major;
import com.example.backend.exception.business.BusinessError;
import com.example.backend.exception.business.BusinessException;
import com.example.backend.repository.MajorRepository;
import com.example.backend.repository.UnitRepository;
import com.example.backend.utils.ResourceUtil;

import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor 
public class MajorService {
    private final MajorRepository majorRepository;
    private final UnitRepository unitRepository;

    public Major newMajor(NewMajorRequest request){
        Optional<Major> optionalMajor = majorRepository.findById(request.getCode());
        if(optionalMajor.isPresent())
            throw new BusinessException(BusinessError.MAJOR_EXISTS, ResourceUtil.createArgs(request.getCode()));

        Major newMajor = new Major(
            request.getCode(),
            request.getName(),
            request.getInstructionLanguage(),
            request.getUnitCode(),
            request.getProgramType()
        );
        try{
            majorRepository.save(newMajor);
        } catch (DataIntegrityViolationException exception){
            throw new BusinessException(BusinessError.UNIT_NOT_FOUND, ResourceUtil.createArgs(request.getCode()));
        }
        return newMajor;
    }

    public List<Major> getAllMajors(){
        List<Major> majors = majorRepository.findAll();
        return  majors;
    }

    public Major getMajor(String code){
        Major major = majorRepository.findById(code).orElseThrow(()->{
            throw new BusinessException(BusinessError.MAJOR_NOT_FOUND, ResourceUtil.createArgs(code));
        });
        return major;
    }

    @Transactional
    public Major patchMajor(String code, PatchMajorRequest request){
        Major major = majorRepository.findById(code).orElseThrow(()->{
            throw new BusinessException(BusinessError.MAJOR_NOT_FOUND, ResourceUtil.createArgs(code));
        });

        if(request.getName() != null)
            major.setName(request.getName().trim());

        if(request.getInstructionLanguage() != null)
            major.setInstructionLanguage(request.getInstructionLanguage().trim());

        if(request.getProgramType() != null)
            major.setProgramType(request.getProgramType().trim());

        if(request.getUnitCode() != null){
            String unitCode = request.getUnitCode().trim();
            if(!unitRepository.existsById(unitCode))
                throw new BusinessException(BusinessError.UNIT_NOT_FOUND, ResourceUtil.createArgs(unitCode));
            major.setUnitCode(unitCode);
        }

        try{
            return majorRepository.saveAndFlush(major);
        } catch (DataIntegrityViolationException exception){
            throw new BusinessException(BusinessError.UPDATE_MAJOR_FAILED);
        }
    }

    public void deleteMajor(String code){
        majorRepository.deleteById(code);
    }
}

package com.spammayo.spam.study.controller;

import com.spammayo.spam.study.dto.StudyDto;
import com.spammayo.spam.study.entity.Study;
import com.spammayo.spam.study.mapper.StudyMapper;
import com.spammayo.spam.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyMapper mapper;

    @PostMapping
    public ResponseEntity postStudy(@RequestBody StudyDto.InputDto postDto) {
        Study study = studyService.createStudy(mapper.inputDtoToStudy(postDto));
        return new ResponseEntity<>(mapper.studyToSimpleResponseDto(study), HttpStatus.CREATED);
    }

    @PatchMapping("{study-id}")
    public ResponseEntity patchStudy(@PathVariable("study-id") long studyId,
                                     @RequestBody StudyDto.PatchDto patchDto) {
        patchDto.setStudyId(studyId);
//        studyService.updateStudy(mapper.inputDtoToStudy(patchDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{study-id}")
    public ResponseEntity getStudy(@PathVariable("study-id") long studyId) {
        Study study = studyService.findStudy(studyId);
        return new ResponseEntity<>(mapper.studyToResponseDto(study), HttpStatus.OK);
    }

    @DeleteMapping("/{study-id}")
    public ResponseEntity deleteStudy(@PathVariable("study-id") long studyId) {
        studyService.deleteStudy(studyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

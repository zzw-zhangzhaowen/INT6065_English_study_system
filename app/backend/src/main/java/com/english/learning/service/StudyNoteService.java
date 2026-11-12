package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.StudyNoteRequest;
import com.english.learning.entity.StudyNote;
import com.english.learning.mapper.StudyNoteMapper;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StudyNoteService {
    private static final Logger log = LoggerFactory.getLogger(StudyNoteService.class);
    private final StudyNoteMapper studyNoteMapper;

    public Page<StudyNote> getNotePage(int pageNum, int pageSize, Long studentId, String category) {
        Page<StudyNote> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<StudyNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(studentId != null, StudyNote::getStudentId,studentId).eq(category != null, StudyNote::getCategory,category).orderByDesc(StudyNote::getCreateTime);
        return (Page<StudyNote>)this.studyNoteMapper.selectPage((IPage<StudyNote>)page, wrapper);
    }

    public Page<StudyNote> getSharedNotePage(int pageNum, int pageSize, String category) {
        Page<StudyNote> page = new Page<>((long)pageNum, (long)pageSize);
        LambdaQueryWrapper<StudyNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudyNote::getIsShared,1).eq(category != null, StudyNote::getCategory,category).orderByDesc(StudyNote::getViewCount);
        return (Page<StudyNote>)this.studyNoteMapper.selectPage((IPage<StudyNote>)page, wrapper);
    }

    public StudyNote getNoteDetail(Long noteId) {
        StudyNote note = (StudyNote)this.studyNoteMapper.selectById(noteId);
        if (note == null) {
            throw new BusinessException("\u7b14\u8bb0\u4e0d\u5b58\u5728");
        }
        note.setViewCount(Integer.valueOf(note.getViewCount() + 1));
        this.studyNoteMapper.updateById(note);
        return note;
    }

    public void createNote(Long studentId, StudyNoteRequest request) {
        StudyNote note = new StudyNote();
        note.setStudentId(studentId);
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCategory(request.getCategory());
        note.setTags(request.getTags());
        note.setIsShared(Integer.valueOf(request.getIsShared() != null ? request.getIsShared() : 0));
        note.setViewCount(0);
        note.setLikeCount(0);
        this.studyNoteMapper.insert(note);
    }

    public void updateNote(Long noteId, Long studentId, StudyNoteRequest request) {
        StudyNote note = (StudyNote)this.studyNoteMapper.selectById(noteId);
        if (note == null) {
            throw new BusinessException("\u7b14\u8bb0\u4e0d\u5b58\u5728");
        }
        if (!note.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u4fee\u6539\u6b64\u7b14\u8bb0");
        }
        if (request.getTitle() != null) {
            note.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            note.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            note.setCategory(request.getCategory());
        }
        if (request.getTags() != null) {
            note.setTags(request.getTags());
        }
        if (request.getIsShared() != null) {
            note.setIsShared(request.getIsShared());
        }
        this.studyNoteMapper.updateById(note);
    }

    public void deleteNote(Long noteId, Long studentId) {
        StudyNote note = (StudyNote)this.studyNoteMapper.selectById(noteId);
        if (note == null) {
            throw new BusinessException("\u7b14\u8bb0\u4e0d\u5b58\u5728");
        }
        if (!note.getStudentId().equals(studentId)) {
            throw new BusinessException("\u65e0\u6743\u5220\u9664\u6b64\u7b14\u8bb0");
        }
        this.studyNoteMapper.deleteById(noteId);
    }

    public void likeNote(Long noteId) {
        StudyNote note = (StudyNote)this.studyNoteMapper.selectById(noteId);
        if (note == null) {
            throw new BusinessException("\u7b14\u8bb0\u4e0d\u5b58\u5728");
        }
        note.setLikeCount(Integer.valueOf(note.getLikeCount() + 1));
        this.studyNoteMapper.updateById(note);
    }

    public StudyNoteService(StudyNoteMapper studyNoteMapper) {
        this.studyNoteMapper = studyNoteMapper;
    }
}


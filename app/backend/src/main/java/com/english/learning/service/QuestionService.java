package com.english.learning.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.english.learning.common.exception.BusinessException;
import com.english.learning.dto.QuestionRequest;
import com.english.learning.entity.Question;
import com.english.learning.mapper.QuestionMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class QuestionService {
    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionMapper questionMapper;

    public Page<Question> getQuestionPage(int pageNum, int pageSize, Long categoryId, String questionType, Integer difficulty, String keyword) {
        Page<Question> page = new Page<>((long)pageNum, (long)pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return this.questionMapper.selectPageWithCategory(page, categoryId, questionType, difficulty, keyword.trim());
        }
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Question::getCategoryId,categoryId).eq(questionType != null && !questionType.isEmpty(), Question::getQuestionType,questionType).eq(difficulty != null, Question::getDifficulty,difficulty).eq(Question::getStatus,1).orderByDesc(Question::getCreateTime);
        return (Page<Question>)this.questionMapper.selectPage((IPage<Question>)page, wrapper);
    }

    @Transactional(rollbackFor={Exception.class})
    public void createQuestion(QuestionRequest request, Long creatorId) {
        Question question = new Question();
        question.setCategoryId(request.getCategoryId());
        question.setQuestionType(request.getQuestionType());
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setPassageId(request.getPassageId());
        question.setOptions(request.getOptions());
        question.setAnswer(request.getAnswer());
        question.setAnalysis(request.getAnalysis());
        question.setDifficulty(Integer.valueOf(request.getDifficulty() != null ? request.getDifficulty() : 1));
        question.setScore(request.getScore() != null ? request.getScore() : BigDecimal.ONE);
        question.setAudioUrl(request.getAudioUrl());
        question.setVideoUrl(request.getVideoUrl());
        question.setImageUrl(request.getImageUrl());
        question.setKeywords(request.getKeywords());
        question.setCreatorId(creatorId);
        question.setUsageCount(0);
        question.setCorrectRate(BigDecimal.ZERO);
        question.setStatus(1);
        this.questionMapper.insert(question);
    }

    @Transactional(rollbackFor={Exception.class})
    public void updateQuestion(Long id, QuestionRequest request) {
        Question existing = (Question)this.questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u9898\u76ee\u4e0d\u5b58\u5728");
        }
        if (request.getCategoryId() != null) {
            existing.setCategoryId(request.getCategoryId());
        }
        if (request.getQuestionType() != null) {
            existing.setQuestionType(request.getQuestionType());
        }
        if (request.getTitle() != null) {
            existing.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            existing.setContent(request.getContent());
        }
        if (request.getPassageId() != null) {
            existing.setPassageId(request.getPassageId());
        }
        if (request.getOptions() != null) {
            existing.setOptions(request.getOptions());
        }
        if (request.getAnswer() != null) {
            existing.setAnswer(request.getAnswer());
        }
        if (request.getAnalysis() != null) {
            existing.setAnalysis(request.getAnalysis());
        }
        if (request.getDifficulty() != null) {
            existing.setDifficulty(request.getDifficulty());
        }
        if (request.getScore() != null) {
            existing.setScore(request.getScore());
        }
        if (request.getAudioUrl() != null) {
            existing.setAudioUrl(request.getAudioUrl());
        }
        if (request.getVideoUrl() != null) {
            existing.setVideoUrl(request.getVideoUrl());
        }
        if (request.getImageUrl() != null) {
            existing.setImageUrl(request.getImageUrl());
        }
        if (request.getKeywords() != null) {
            existing.setKeywords(request.getKeywords());
        }
        this.questionMapper.updateById(existing);
    }

    @Transactional(rollbackFor={Exception.class})
    public void deleteQuestion(Long id) {
        Question existing = (Question)this.questionMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("\u9898\u76ee\u4e0d\u5b58\u5728");
        }
        this.questionMapper.deleteById(id);
    }

    public Question getQuestionById(Long id) {
        Question question = (Question)this.questionMapper.selectById(id);
        if (question == null) {
            throw new BusinessException("\u9898\u76ee\u4e0d\u5b58\u5728");
        }
        return question;
    }

    @Transactional(rollbackFor={Exception.class})
    public int importQuestions(MultipartFile file, Long categoryId) {
        log.info("\u5bfc\u5165\u9898\u76ee\u6587\u4ef6: {}",file.getOriginalFilename());
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
            throw new BusinessException("\u8bf7\u4e0a\u4f20Excel\u6587\u4ef6(.xlsx\u6216.xls)");
        }
        ArrayList<Question> questions = new ArrayList<Question>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            for (int i = 1; i < rowCount; ++i) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Question question = new Question();
                question.setCategoryId(categoryId != null ? categoryId : this.getLongCellValue(row.getCell(0)));
                question.setQuestionType(this.getStringCellValue(row.getCell(1)));
                question.setTitle(this.getStringCellValue(row.getCell(2)));
                question.setContent(this.getStringCellValue(row.getCell(3)));
                question.setOptions(this.getStringCellValue(row.getCell(4)));
                question.setAnswer(this.getStringCellValue(row.getCell(5)));
                question.setAnalysis(this.getStringCellValue(row.getCell(6)));
                question.setDifficulty(Integer.valueOf(this.getIntCellValue(row.getCell(7), 1)));
                question.setScore(new BigDecimal(this.getDoubleCellValue(row.getCell(8), 1.0)));
                question.setKeywords(this.getStringCellValue(row.getCell(9)));
                question.setUsageCount(0);
                question.setCorrectRate(BigDecimal.ZERO);
                question.setStatus(1);
                if (question.getTitle() == null || question.getTitle().isEmpty()) continue;
                questions.add(question);
            }
            int batchSize = 500;
            for (int i = 0; i < questions.size(); i += batchSize) {
                List<Question> batch = questions.subList(i, Math.min(i + batchSize, questions.size()));
                for (Question question : batch) {
                    this.questionMapper.insert(question);
                }
            }
            log.info("\u6210\u529f\u5bfc\u5165 {} \u9053\u9898\u76ee",questions.size());
        } catch (IOException e) {
            log.error("\u8bfb\u53d6Excel\u6587\u4ef6\u5931\u8d25", e);
            throw new BusinessException("\u8bfb\u53d6Excel\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
        return questions.size();
    }

    public byte[] exportQuestions(Long categoryId, String questionType, Integer difficulty) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Question::getCategoryId,categoryId).eq(questionType != null, Question::getQuestionType,questionType).eq(difficulty != null, Question::getDifficulty,difficulty).eq(Question::getStatus,1).orderByDesc(Question::getCreateTime);
        List<Question> questions = this.questionMapper.selectList(wrapper);
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("\u9898\u76ee\u5217\u8868");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row headerRow = sheet.createRow(0);
            String[] headers = new String[]{"\u5206\u7c7bID", "\u9898\u578b", "\u6807\u9898", "\u5185\u5bb9", "\u9009\u9879(JSON)", "\u7b54\u6848", "\u89e3\u6790", "\u96be\u5ea6", "\u5206\u503c", "\u5173\u952e\u8bcd"};
            for (int i = 0; i < headers.length; ++i) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (Question question : questions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(question.getCategoryId() != null ? (double)question.getCategoryId().longValue() : 0.0);
                row.createCell(1).setCellValue(question.getQuestionType() != null ? question.getQuestionType() : "");
                row.createCell(2).setCellValue(question.getTitle() != null ? question.getTitle() : "");
                row.createCell(3).setCellValue(question.getContent() != null ? question.getContent() : "");
                row.createCell(4).setCellValue(question.getOptions() != null ? question.getOptions() : "");
                row.createCell(5).setCellValue(question.getAnswer() != null ? question.getAnswer() : "");
                row.createCell(6).setCellValue(question.getAnalysis() != null ? question.getAnalysis() : "");
                row.createCell(7).setCellValue(question.getDifficulty() != null ? (double)question.getDifficulty().intValue() : 1.0);
                row.createCell(8).setCellValue(question.getScore() != null ? question.getScore().doubleValue() : 1.0);
                row.createCell(9).setCellValue(question.getKeywords() != null ? question.getKeywords() : "");
            }
            for (int i = 0; i < headers.length; ++i) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write((OutputStream)outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("\u5bfc\u51faExcel\u6587\u4ef6\u5931\u8d25", e);
            throw new BusinessException("\u5bfc\u51faExcel\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
    }

    public byte[] downloadTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("\u9898\u76ee\u5bfc\u5165\u6a21\u677f");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row headerRow = sheet.createRow(0);
            String[] headers = new String[]{"\u5206\u7c7bID", "\u9898\u578b", "\u6807\u9898", "\u5185\u5bb9", "\u9009\u9879(JSON)", "\u7b54\u6848", "\u89e3\u6790", "\u96be\u5ea6(1-3)", "\u5206\u503c", "\u5173\u952e\u8bcd"};
            for (int i = 0; i < headers.length; ++i) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue(1.0);
            exampleRow.createCell(1).setCellValue("\u5355\u9009");
            exampleRow.createCell(2).setCellValue("What is the capital of France?");
            exampleRow.createCell(3).setCellValue("Choose the correct answer.");
            exampleRow.createCell(4).setCellValue("[\"A. London\",\"B. Paris\",\"C. Berlin\",\"D. Rome\"]");
            exampleRow.createCell(5).setCellValue("B");
            exampleRow.createCell(6).setCellValue("Paris is the capital of France.");
            exampleRow.createCell(7).setCellValue(1.0);
            exampleRow.createCell(8).setCellValue(2.0);
            exampleRow.createCell(9).setCellValue("geography,capital");
            for (int i = 0; i < headers.length; ++i) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write((OutputStream)outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("\u751f\u6210\u6a21\u677f\u6587\u4ef6\u5931\u8d25", e);
            throw new BusinessException("\u751f\u6210\u6a21\u677f\u6587\u4ef6\u5931\u8d25: " + e.getMessage());
        }
    }

    public List<Question> getRandomQuestions(Long categoryId, String questionType, Integer difficulty, int count) {
        count = Math.min(count, 100);
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Question::getCategoryId,categoryId).eq(questionType != null, Question::getQuestionType,questionType).eq(difficulty != null, Question::getDifficulty,difficulty).eq(Question::getStatus,1).last("ORDER BY RAND() LIMIT " + count);
        return this.questionMapper.selectList(wrapper);
    }

    public Map<String, Object> getQuestionStats() {
        HashMap<String, Object> stats = new HashMap<>();
        long totalCount = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getStatus,1).eq(Question::getDeleted,0));
        stats.put("totalCount", totalCount);
        Map<String, Long> typeStats = new HashMap<>();
        List<Map<String, Object>> typeRows = this.questionMapper.countGroupByType();
        for (Map<String, Object> row : typeRows) {
            String type = row.get("questionType") != null ? row.get("questionType").toString() : "OTHER";
            Long cnt = ((Number)row.get("cnt")).longValue();
            typeStats.put(type, cnt);
        }
        stats.put("typeStats", typeStats);
        Map<Integer, Long> difficultyStats = new HashMap<>();
        List<Map<String, Object>> diffRows = this.questionMapper.countGroupByDifficulty();
        for (Map<String, Object> row : diffRows) {
            Integer diff = ((Number)row.get("difficulty")).intValue();
            Long cnt = ((Number)row.get("cnt")).longValue();
            difficultyStats.put(diff, cnt);
        }
        stats.put("difficultyStats", difficultyStats);
        long todayCount = this.questionMapper.selectCount(new LambdaQueryWrapper<Question>().eq(Question::getStatus,1).ge(Question::getCreateTime,LocalDate.now().atStartOfDay()));
        stats.put("todayCount", todayCount);
        return stats;
    }

    @Transactional(rollbackFor={Exception.class})
    public void batchDeleteQuestions(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        this.questionMapper.delete(new LambdaQueryWrapper<Question>().in(Question::getId, ids));
        log.info("\u6279\u91cf\u5220\u9664\u9898\u76ee: {}", ids);
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val) && !Double.isInfinite(val)) {
                return String.valueOf((long)val);
            }
            return String.valueOf(val);
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        }
        return null;
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long)cell.getNumericCellValue();
        }
        try {
            return Long.parseLong(cell.getStringCellValue().trim());
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private int getIntCellValue(Cell cell, int defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int)cell.getNumericCellValue();
        }
        try {
            return Integer.parseInt(cell.getStringCellValue().trim());
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private double getDoubleCellValue(Cell cell, double defaultValue) {
        if (cell == null) {
            return defaultValue;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        try {
            return Double.parseDouble(cell.getStringCellValue().trim());
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public QuestionService(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }
}


# 测试考试提交评分功能
# 步骤1: 先登录获取token
Write-Host "========== 步骤1: 登录获取token ==========" -ForegroundColor Cyan
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"username":"student1","password":"123456"}'
$token = $loginResponse.data.token
Write-Host "Token: $token" -ForegroundColor Green

# 步骤2: 提交考试答案
Write-Host "`n========== 步骤2: 提交考试答案 ==========" -ForegroundColor Cyan
Write-Host "题目216正确答案: B, 提交答案: 1 (数字)" -ForegroundColor Yellow
Write-Host "题目217正确答案: C, 提交答案: 2 (数字)" -ForegroundColor Yellow  
Write-Host "题目218正确答案: B, 提交答案: 0 (数字-错误)" -ForegroundColor Yellow

$submitData = @{
    recordId = 999
    answers = @(
        @{ questionId = 216; answer = "1"; answerTime = 0 },
        @{ questionId = 217; answer = "2"; answerTime = 0 },
        @{ questionId = 218; answer = "0"; answerTime = 0 }
    )
} | ConvertTo-Json -Depth 10

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $submitResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/exam/submit" -Method Post -Headers $headers -Body $submitData
    Write-Host "`n提交结果:" -ForegroundColor Green
    Write-Host ($submitResponse | ConvertTo-Json -Depth 10) -ForegroundColor White
} catch {
    Write-Host "提交失败: $_" -ForegroundColor Red
    Write-Host "响应内容: $($_.Exception.Response)" -ForegroundColor Red
}

# 步骤3: 查看数据库中的分数
Write-Host "`n========== 步骤3: 验证数据库分数 ==========" -ForegroundColor Cyan
$dbResult = mysql -uroot -p123456 english_learning -e "SELECT id, obtained_score, correct_count, wrong_count, status FROM exam_record WHERE id=999;" 2>$null
Write-Host $dbResult

Write-Host "`n========== 步骤4: 查看答题详情 ==========" -ForegroundColor Cyan
$answerResult = mysql -uroot -p123456 english_learning -e "SELECT question_id, student_answer, is_correct, obtained_score FROM answer_record WHERE exam_record_id=999;" 2>$null
Write-Host $answerResult

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from webdriver_manager.chrome import ChromeDriverManager
import time
import json
import sys

def searchProblem(userId, problemNumber, targetSubmissionId):
    # WebDriver 설정
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service)

    try:
        # BOJ 사이트 접속
        driver.get('https://www.acmicpc.net/status')

        # 페이지가 로드될 때까지 기다리기
        time.sleep(0.2)

        # 문제 검색창 찾기 (NAME 속성을 사용하여)
        searchBox = driver.find_element(By.NAME, 'problem_id')

        # 문제 번호 입력
        searchBox.send_keys(problemNumber)

        # 아이디 검색창 찾기 (NAME 속성을 사용하여)
        searchBoxId = driver.find_element(By.NAME, 'user_id')

        # id 입력
        searchBoxId.send_keys(userId)

        # 검색 실행
        searchBox.send_keys(Keys.RETURN)

        # 검색 결과 로딩 기다리기
        time.sleep(0.2)

        # userId와 problemNumber로 가져온 제출 기록 찾기
        rows = driver.find_elements(By.CSS_SELECTOR, 'tbody tr')
        result_json = {
            'submissionId': targetSubmissionId,
            'result': '제출 기록이 없습니다.',
            'memory': '',
            'time': '',
            'codeType': '',
            'codeLength': ''
        }

        for row in rows:
            submissionId = row.find_element(By.CSS_SELECTOR, 'td:nth-child(1)').text
            if submissionId == targetSubmissionId:
                result_json = {
                    'submissionId': submissionId,
                    'result': row.find_element(By.CSS_SELECTOR, 'td:nth-child(4)').text,
                    'memory': row.find_element(By.CSS_SELECTOR, 'td:nth-child(5)').text,
                    'time': row.find_element(By.CSS_SELECTOR, 'td:nth-child(6)').text,
                    'codeType': row.find_element(By.CSS_SELECTOR, 'td:nth-child(7)').text,
                    'codeLength': row.find_element(By.CSS_SELECTOR, 'td:nth-child(8)').text
                }
                break  # 일치하는 결과를 찾았으므로 반복문을 종료

        # 결과를 JSON 객체 형태로 출력
        print(json.dumps(result_json, ensure_ascii=False, indent=4))
    finally:
        # 브라우저 닫기
        driver.quit()
if __name__ == "__main__":
    userId = sys.argv[1]
    problemNumber = sys.argv[2]
    targetSubmissionId = sys.argv[3]
    searchProblem(userId, problemNumber, targetSubmissionId)
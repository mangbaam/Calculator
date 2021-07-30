# Calculator
Calculator with history. extendsion function, db with room, layoutInflater
첫 번째 숫자와 연산자를 입력 후 두 번째 숫자를 입력할 때마다 연산 결과가 미리보기로 화면에 보여진다.
![Screenshot_1627288784](https://user-images.githubusercontent.com/44221447/126961692-e64fc784-4a05-4bd0-bc41-c9e1355cb082.png)


'=' 버튼을 눌러 계산을 하면 계산 결과가 expression 창으로 올라오고, DB에 저장되어 좌측 하단 히스토리 버튼을 누르면 결과를 확인할 수 있다.
![Screenshot_1627288858](https://user-images.githubusercontent.com/44221447/126961700-e3dab3f1-eeeb-40d9-9420-d9342b542e53.png)


## 2021.07.27

클론 코딩에서는 연산자 하나만 사용할 수 있었다.
심지어 괄호나 소수를 위한 . 사용도 불가능했다.

더 완벽한 계산기를 위해서 이번 커밋에서 괄호 기능을 추가했다. (아직 계산은 안된다 -> 소수까지 표현한 후 추가 예정)

기존에 연산자 하나만을 사용하는 구조에서는 ' '(공백문자)로 split을 하여 두 수와 연산자를 구분해냈지만 연산자가 여러 개 필요하고, 괄호까지 도입된다면 훨씬 복잡한 과정을 거쳐야 한다.
그렇기 때문에 괄호 하나를 넣기 위해 기존의 구조를 완전히 뒤바꿨다.

다음 커밋에서는 소수점을 표현하고, 가능하면 계산 기능까지 도전할 예정


## 2021.07.28

이번 커밋에서는 소수점을 표현했고, 원활한 계산을 위해 내부 구조를 바꿔야 했다.
기존에는 String으로 계산식을 다루었지만 List 형태로 변경해서 각 요소에 접근하기 용이하도록 하였다.
그리고 계산식을 가지고 어떻게 계산을 할까 고민하다가 자료구조 시간에 배운 postfix를 이용하기로 하고 infix인 계산식을 postfix로 바꾸는 함수를 작성하였다.

다음 커밋에서는 실제 계산 기능을 도입하고, 자잘한 오류를 잡을 예정

## 2021.07.29
이번 커밋에서는 실제 계산 기능을 도입했다.
### 업데이트 내용
- BigDecimal 형태로 계산하기 때문에 아주 큰 수도 계산 가능
- 무한소수점의 경우 11번째 자리에서 반올림(10자리까지 표현)
- 큰 수를 보기 쉽게 3자리마다 ,를 찍어 구분 (Locate.US)
- 괄호가 다 닫히지 않은 상태에서도 계산 결과를 미리보기 위해 부족한 괄호 짝만큼 임의로 닫아줘서 계산결과 미리보기 가능
- 소수인 경우 소수점 끝이 0이면 0을 제거 (3.14000 -> 3.14)

### 잡아야 할 오류
- 괄호 다음에 숫자가 오면 앱이 죽는다. 예외 처리 필요
- 소수 입력 중 . 을 입력하면 바로 보이지 않고, 소수점 밑 숫자까지 입력해야 보인다 
    -> 3자리마다 구분하기 위해 NumberFormat을 사용했는데 NumberFormat을 사용하면 123. -> 123으로 반환해서 .이 보이지 않는 것.

### 다음 업데이트 예정
- 연산자 입력 시 연산자에 연두색을 칠하기 위해 getExpressionText() 함수의 반환값을 String에서 SpannableBuilder로 변경
- Text의 양에 따라 글자 크기 조정
- 입력한 수식 하나씩 지우는 버튼
- 오류 잡기

## 2021.07.30
### 업데이트 내용
- 기능 업데이트는 없고, *버그*를 대부분 잡았다.
- 3자리마다 ,를 찍은 것 때문에 계산 과정에서 ,가 들어간 숫자를 숫자로 인식하지 못해 오류가 발생
- 괄호 문제는 깊은 복사가 원인이었음
->원본 리스트는 그대로 두고 그 값만 복사해서 postfix로 변경하려고 했는데 = 으로 바로 할당해서 깊은 복사가 되어서 원본 리스트가 변경되어 식이 계속 깨진 것
- History 기능에 오류가 있었음 -> 계산식과 계산결과가 저장되어야 하는데 계산 결과만 두 번 저장되거나 정상적으로 저장되거나 아예 계산 식이 공백으로 저장되는 오류
-> 메인 스레드와 DB에 저장하는 스레드가 따로 돌기 때문에 발생한 오류로 판단
-> DB 스레드를 돌리기 전에 변수에 값을 저장해놓고 그 변수 값을 DB에 저장하여 해결

### 다음 업데이트 예정
- 앞서 언급한 내용들
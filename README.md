# Calculator
Calculator with history. extendsion function, db with room, layoutInflater
첫 번째 숫자와 연산자를 입력 후 두 번째 숫자를 입력할 때마다 연산 결과가 미리보기로 화면에 보여진다.
![Screenshot_1627288784](https://user-images.githubusercontent.com/44221447/126961692-e64fc784-4a05-4bd0-bc41-c9e1355cb082.png)


'=' 버튼을 눌러 계산을 하면 계산 결과가 expression 창으로 올라오고, DB에 저장되어 좌측 하단 히스토리 버튼을 누르면 결과를 확인할 수 있다.
![Screenshot_1627288858](https://user-images.githubusercontent.com/44221447/126961700-e3dab3f1-eeeb-40d9-9420-d9342b542e53.png)


2021.07.27

클론 코딩에서는 연산자 하나만 사용할 수 있었다.
심지어 괄호나 소수를 위한 . 사용도 불가능했다.

더 완벽한 계산기를 위해서 이번 커밋에서 괄호 기능을 추가했다. (아직 계산은 안된다 -> 소수까지 표현한 후 추가 예정)

기존에 연산자 하나만을 사용하는 구조에서는 ' '(공백문자)로 split을 하여 두 수와 연산자를 구분해냈지만 연산자가 여러 개 필요하고, 괄호까지 도입된다면 훨씬 복잡한 과정을 거쳐야 한다.
그렇기 때문에 괄호 하나를 넣기 위해 기존의 구조를 완전히 뒤바꿨다.

다음 커밋에서는 소수점을 표현하고, 가능하면 계산 기능까지 도전할 예정

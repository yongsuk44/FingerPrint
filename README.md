# FingerPrint

지문 인증 받는 방법 

-- 순서 --

1. 프로젝트의 매니페스트 파일에 지문 인증 권한을 추가
2. App이 실행되는 장치의 잠금 화면이 PIN 이나 패턴 또는 암호로 보호되고 있는지 체크해줘야함 ( 지문은 잠금 화면으로 보호되는 장치에서만 등록 가능)
3. 장치에 최소한 하나의 인증용 지문이 등록되어 있는지 체크
4. FingerprintManager 클래스의 인스턴스 생성
5. Keystore 인스턴스를 사용하여 Android Keystore 컨테이너를 액세스함 ( Keystore 컨테이너는 암호 KEY로 보호되는 Android의 storage 영역임 )
6. KeyGenerator 클래스를 사용해서 암호 KEY를 생성하고 그것을 KeyStore 컨테이너에 저장한다.
7. 생성된 암호 KEY를 사용해서 Cipher 클래스의 인스턴스를 초기화 시켜주어 사용할 준비를 해준다.
8. Cipher 인스턴스를 사용하여 CryptoObject를 생성한다. 그리고 생성한 CryptoObject를 FingerprintManager 인스턴스에 지정해준다.
9. FingerprintManger 인스턴스의 인증 메소드를 호출함
10. 인증 메소드 수행되고 난 후에 호출되는 콜백 메소드를 구현하여 처리하는 기능을 구현

자세한건 Log 및 주석처리 

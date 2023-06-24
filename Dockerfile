FROM google/dart

# 필요한 패키지 설치
RUN apt-get update && \
    apt-get install -y wget unzip openjdk-11-jdk

# Android SDK 다운로드 및 설치
ENV ANDROID_COMPILE_SDK="33"
ENV ANDROID_BUILD_TOOLS="33.0.2"
ENV ANDROID_SDK_TOOLS="7583922"
ENV ANDROID_HOME="/android-sdk"

# Android SDK 다운로드
RUN wget --quiet --output-document=commandlinetools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip && \
    unzip -d cmdline-tools commandlinetools.zip && \
    rm commandlinetools.zip

# Android SDK 설치
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    mv cmdline-tools/* ${ANDROID_HOME}/cmdline-tools/latest && \
    rm -rf cmdline-tools

# Android SDK 경로 설정
ENV PATH="${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${PATH}"
ENV ANDROID_SDK_ROOT="${ANDROID_HOME}"
ENV ANDROID_SDK_HOME="${ANDROID_HOME}"

# Java 환경 변수 설정
ENV JAVA_HOME="/usr/lib/jvm/java-11-openjdk-amd64"
ENV PATH="${JAVA_HOME}/bin:${PATH}"

WORKDIR /app

# 앱 소스 코드 복사
COPY . /app

# SDK 라이선스 동의
RUN yes | sdkmanager --licenses

# 앱 빌드
RUN ./gradlew assembleDebug

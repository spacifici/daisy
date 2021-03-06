FROM ubuntu:18.04

ENV ANDROID_SDK_ROOT=/sdk/android-sdk
RUN apt-get update && \
    apt-get -y --no-install-recommends install \
        build-essential \
        openjdk-8-jdk-headless \
        curl \
        unzip \
        ruby-full \
        locales && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
RUN locale-gen en_US en_US.UTF-8
ENV LC_ALL=en_US.UTF-8 \
    LANG=en_US.UTF-8
RUN mkdir -p /tmp "$ANDROID_SDK_ROOT" && \
    curl -o /tmp/sdk-tools-linux.zip "https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip" && \
    (cd "$ANDROID_SDK_ROOT"; unzip /tmp/sdk-tools-linux.zip) && \
    rm /tmp/sdk-tools-linux.zip && \
    yes|"$ANDROID_SDK_ROOT/tools/bin/sdkmanager" --licenses && \
    "$ANDROID_SDK_ROOT/tools/bin/sdkmanager" --install \
        "build-tools;28.0.3" \
        "platform-tools" \
        "platforms;android-28" \
        "platforms;android-29" \
        "ndk;21.0.6113669"
RUN gem install bundler:1.17

ARG UID=1000
ARG GID=1000
RUN getent group $GID || groupadd user --gid $GID
RUN getent passwd $UID || useradd --create-home --shell /bin/bash user --uid $UID --gid $GID
USER user
RUN mkdir /home/user/bundle && bundle config path /home/user/bundle


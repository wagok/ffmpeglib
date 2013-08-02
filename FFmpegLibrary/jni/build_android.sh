#!/bin/bash
#
# build_android.sh
# Copyright (c) 2012 Jacek Marchwicki
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#export NDK=/home/wlad/Development/adt-bundle-linux/android-ndk-r8e/

if [ "$NDK" = "" ]; then
	echo NDK variable not set, exiting
	echo "Use: export NDK=/your/path/to/android-ndk"
	exit 1
fi

OS=`uname -s | tr '[A-Z]' '[a-z]'`

SYSROOT=${NDK}/platforms/android-9/arch-arm
TOOLCHAIN_VERSION=4.7
X264_CROSS_PREFIX=${NDK}/toolchains/arm-linux-androideabi-${TOOLCHAIN_VERSION}/prebuilt/${OS}-x86/bin/arm-linux-androideabi-



function build_x264()
{
   PLATFORM=$NDK/platforms/$PLATFORM_VERSION/arch-$ARCH/
	echo "Building x264..."
    	cd x264
    	./configure \
    	    --cross-prefix=${X264_CROSS_PREFIX} \
    	    --sysroot=${PLATFORM} \
    	    --prefix=$(pwd)/$PREFIX \
    	    --host=$ARCH-linux \
    	    --enable-static \
    	    --enable-strip \
    	    --disable-cli \
    	    --disable-avs \
    	    --disable-swscale \
    	    --disable-lavf \
    	    --disable-ffms \
    	    --disable-gpac \
    	    --disable-interlaced \
    	    --chroma-format=420 \
    	    --disable-shared \
            --enable-static \
    	    --enable-pic \
    	    --extra-cflags="-D_ANDROID_SYS_ -fno-tree-vectorize -mvectorize-with-neon-quad -funsafe-math-optimizations ${OPTIMIZE_CFLAGS}" \
    	    --extra-ldflags="-Wl,-rpath-link=${SYSROOT}/usr/lib -L${SYSROOT}/usr/lib" \
    	    $ADDITIONAL_CONFIGURE_FLAG \
    	    || exit 1

    	make clean || exit 1
    	make V=1 -j4 install || exit 1

    	cd ..
}

function build_ffmpeg
{
	PLATFORM=$NDK/platforms/$PLATFORM_VERSION/arch-$ARCH/
	CC=$PREBUILT/bin/$EABIARCH-gcc
	CROSS_PREFIX=$PREBUILT/bin/$EABIARCH-
	PKG_CONFIG=${CROSS_PREFIX}pkg-config
	if [ ! -f $PKG_CONFIG ];
	then
		cat > $PKG_CONFIG << EOF
#!/bin/bash
pkg-config \$*
EOF
		chmod u+x $PKG_CONFIG
	fi
	NM=$PREBUILT/bin/$EABIARCH-nm
	cd ffmpeg
	export PKG_CONFIG_LIBDIR=$(pwd)/$PREFIX/lib/pkgconfig/
	export PKG_CONFIG_PATH=$(pwd)/$PREFIX/lib/pkgconfig/
	./configure --target-os=linux \
	    --prefix=$PREFIX \
	    --enable-cross-compile \
	    --extra-libs="-lgcc" \
	    --arch=$ARCH \
	    --cc=$CC \
	    --cross-prefix=$CROSS_PREFIX \
	    --nm=$NM \
	    --sysroot=$PLATFORM \
	    --extra-cflags=" -O3 -fpic -DANDROID -DHAVE_SYS_UIO_H=1 -Dipv6mr_interface=ipv6mr_ifindex -fasm -Wno-psabi -fno-short-enums  -fno-strict-aliasing -finline-limit=300 $OPTIMIZE_CFLAGS " \
	    --disable-shared \
	    --enable-static \
	    --enable-runtime-cpudetect \
	    --extra-ldflags="-Wl,-rpath-link=$PLATFORM/usr/lib -L$PLATFORM/usr/lib  -nostdlib -lc -lm -ldl -llog -L$PREFIX/lib" \
	    --extra-cflags="-I$PREFIX/include" \
	    --disable-everything \
	    --enable-libx264 \
	    --enable-hwaccel=h264_vaapi \
	    --enable-hwaccel=h264_dxva2 \
	    --enable-hwaccel=mpeg4_vaapi \
	    --enable-demuxer=mov \
	    --enable-demuxer=h264 \
	    --enable-demuxer=mpegvideo \
	    --enable-demuxer=h263 \
	    --enable-demuxer=matroska \
	    --enable-muxer=mp4 \
	    --enable-muxer=mov \
	    --enable-muxer=matroska \
	    --enable-muxer=h264 \
	    --enable-protocol=jni \
	    --enable-protocol=file \
	    --enable-decoder=mjpeg \
	    --enable-encoder=mjpeg \
	    --enable-decoder=h263 \
	    --enable-decoder=mpeg4 \
	    --enable-encoder=mpeg4 \
	    --enable-decoder=h264 \
	    --enable-encoder=h264 \
	    --enable-decoder=aac \
	    --enable-encoder=aac \
	    --enable-parser=h264 \
	    --enable-encoder=mp2 \
	    --enable-decoder=mp2 \
	    --enable-muxer=mp2 \
        --disable-debug \
        --enable-encoder=libx264  \
	    --enable-bsfs \
	    --enable-decoders \
	    --enable-encoders \
	    --enable-parsers \
	    --enable-hwaccels \
	    --enable-muxers \
	    --enable-avformat \
	    --enable-avcodec \
	    --enable-avresample \
	    --enable-zlib \
	    --disable-doc \
        --disable-ffplay \
	    --disable-ffprobe \
	    --disable-ffserver \
	    --disable-avfilter \
	    --disable-avdevice \
	    --enable-version3 \
	    --enable-memalign-hack \
	    --enable-asm \
	    --enable-gpl \
	    $ADDITIONAL_CONFIGURE_FLAG \
	    || exit 1
	make clean || exit 1
	make -j4 install || exit 1

	cd ..
}



function build_one {
	cd ffmpeg
	PLATFORM=$NDK/platforms/$PLATFORM_VERSION/arch-$ARCH/
	$PREBUILT/bin/$EABIARCH-ld \
	-rpath-link=$PLATFORM/usr/lib \
	-L$PLATFORM/usr/lib -L$PREFIX/lib \
	 -soname $SONAME -shared -nostdlib \
	  -z noexecstack -Bsymbolic --whole-archive \
	   --no-undefined -o $OUT_LIBRARY -lavcodec \
	   -lavformat -lavresample -lavutil -lswresample \
	    -lswscale \
	    -lx264 \
	   -lc -lm -lz -ldl -llog  \
	     --dynamic-linker=/system/bin/linker \
	   -zmuldefs $PREBUILT/lib/gcc/$EABIARCH/${TOOLCHAIN_VERSION}/libgcc.a || exit 1
	cd ..
}

  set -x verbose


function strip {
  cd ffmpeg
  STRIP_FILE=$1
  $PREBUILT/bin/$EABIARCH-strip -s $STRIP_FILE
  cd ..
  }

#arm v7vfpv3
EABIARCH=arm-linux-androideabi
ARCH=arm
CPU=armv7-a
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=vfpv3-d16 -marm -march=$CPU "
PREFIX=../ffmpeg-build/armeabi-v7a
OUT_LIBRARY=$PREFIX/libffmpeg.so
ADDITIONAL_CONFIGURE_FLAG=
SONAME=libffmpeg.so
PREBUILT=$NDK/toolchains/arm-linux-androideabi-${TOOLCHAIN_VERSION}/prebuilt/$OS-x86
PLATFORM_VERSION=android-9
PLATFORM=$NDK/platforms/$PLATFORM_VERSION/arch-$ARCH/
build_x264
build_ffmpeg
build_one
strip OUT_LIBRARY


#arm v7 + neon (neon also include vfpv3-32)
EABIARCH=arm-linux-androideabi
ARCH=arm
CPU=armv7-a
OPTIMIZE_CFLAGS="-mfloat-abi=softfp -mfpu=neon -marm -march=$CPU -mtune=cortex-a8 -mthumb -D__thumb__ "
PREFIX=../ffmpeg-build/armeabi-v7a-neon
OUT_LIBRARY=../ffmpeg-build/armeabi-v7a/libffmpeg-neon.so
ADDITIONAL_CONFIGURE_FLAG=--enable-neon
SONAME=libffmpeg-neon.so
PREBUILT=$NDK/toolchains/arm-linux-androideabi-${TOOLCHAIN_VERSION}/prebuilt/$OS-x86
PLATFORM_VERSION=android-9
PLATFORM=$NDK/platforms/$PLATFORM_VERSION/arch-$ARCH/
build_x264
build_ffmpeg
build_one
strip $OUT_LIBRARY
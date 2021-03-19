import eyed3

import time
import pathlib
from os import path

from eyed3 import id3
from eyed3 import load

from pydub import AudioSegment

import os
print(os.name)

# >> postsix
import platform
print(platform.system())
# >> Linux

# 플랫폼에 맞는 구분자
# Linux: Linux
# Mac: Darwin
# Windows: Windows

if (platform.system() == 'Darwin') :
    AudioSegment.converter = '/usr/local/bin/ffmpeg'
    AudioSegment.ffmpeg = '/usr/local/bin/ffmpeg'
    AudioSegment.ffprobe = '/usr/local/bin/ffprobe'
else:
    AudioSegment.converter = "C:\\Users\\user\\Desktop\\workspace\\library\\ffmpeg\\bin\\ffmpeg.exe"
    AudioSegment.ffmpeg = "C:\\Users\\user\\Desktop\\workspace\\library\\ffmpeg\\bin\\ffmpeg.exe"
    AudioSegment.ffprobe = "C:\\Users\\user\\Desktop\\workspace\\library\\ffmpeg\\bin\\ffprobe.exe"

current_home = path.expanduser('~')
#search_dir = path.join(current_home + "/Music/iTunes/iTunes Media/Music/Rush/Chronicles (Disc 2)/2-03 Limelight.mp3")  # noqa

def track_info(filename):
    """Module Built To Read ID3 Track Data."""
    tag = id3.Tag()
    tag.parse(filename)
    a = load(filename)
    print("# {}".format('=' * 78))
    print("Track Name:     {}".format(tag.title))
    print("Track Artist:   {}".format(tag.artist))
    print("Track Album:    {}".format(tag.album))
    print("Track Duration: {}".format(duration_from_seconds(a.info.time_secs)))
    print("Track Number:   {}".format(tag.track_num))
    print("Track BitRate:  {}".format(a.info.bit_rate))
    print("Track BitRate:  {}".format(a.info.bit_rate_str))
    print("Sample Rate:    {}".format(a.info.sample_freq))
    print("Mode:           {}".format(a.info.mode))
    print("# {}".format('=' * 78))
    print("Album Artist:         {}".format(tag.album_artist))
    print("Album Year:           {}".format(tag.getBestDate()))
    print("Album Recording Date: {}".format(tag.recording_date))
    print("Album Type:           {}".format(tag.album_type))
    print("Disc Num:             {}".format(tag.disc_num))
    print("Artist Origin:        {}".format(tag.artist_origin))
    print("# {}".format('=' * 78))
    print("Artist URL:         {}".format(tag.artist_url))
    print("Audio File URL:     {}".format(tag.audio_file_url))
    print("Audio Source URL:   {}".format(tag.audio_source_url))
    print("Commercial URL:     {}".format(tag.commercial_url))
    print("Copyright URL:      {}".format(tag.copyright_url))
    print("Internet Radio URL: {}".format(tag.internet_radio_url))
    print("Publisher URL:      {}".format(tag.publisher_url))
    print("Payment URL:        {}".format(tag.payment_url))
    print("# {}".format('=' * 78))
    print("Publisher: {}".format(tag.publisher))
    print("Original Release Date: {}".format(tag.original_release_date))
    print("Play Count: {}".format(tag.play_count))
    print("Tagging Date: {}".format(tag.tagging_date))
    print("Release Date: {}".format(tag.release_date))
    print("Terms Of Use: {}".format(tag.terms_of_use))
    print("isV1: {}".format(tag.isV1()))
    print("isV2: {}".format(tag.isV2()))
    print("BPM: {}".format(tag.bpm))
    print("Cd Id: {}".format(tag.cd_id))
    print("Composer: {}".format(tag.composer))
    print("Encoding date: {}".format(tag.encoding_date))
    print("# {}".format('=' * 78))
    if tag.genre is not None : print("Genre: {}".format(tag.genre.name))
    if tag.non_std_genre is not None : print("Non Std Genre Name: {}".format(tag.non_std_genre.name))
    if tag.genre is not None : print("Genre ID: {}".format(tag.genre.id))
    if tag.non_std_genre is not None : print("Non Std Genre ID: {}".format(tag.non_std_genre.id))
    print("LAME Tag:       {}".format(a.info.lame_tag))
    print("# {}".format('=' * 78))
    print("Header Version: {}".format(tag.header.version))
    print("Header Major Version: {}".format(tag.header.major_version))
    print("Header Minor Version: {}".format(tag.header.minor_version))
    print("Header Rev Version: {}".format(tag.header.rev_version))
    print("Header Extended: {}".format(tag.header.extended))
    print("Header Footer: {}".format(tag.header.footer))
    print("Header Experimental: {}".format(tag.header.experimental))
    print("Header SIZE: {}".format(tag.header.SIZE))
    print("Header Tag Size: {}".format(tag.header.tag_size))
    print("Extended Header Size: {}".format(tag.extended_header.size))
    print("# {}".format('=' * 78))
    print("File Name: {}".format(tag.file_info.name))
    print("File Tag Size: {}".format(tag.file_info.tag_size))
    print("File Tag Padding Size: {}".format(tag.file_info.tag_padding_size))
    print("File Read Only: {}".format(tag.read_only))
    print("File Size: {}".format(a.info.size_bytes))
    print("Last Modified: {}".format(time.strftime('%Y-%m-%d %H:%M:%S',
                                     time.localtime(tag.file_info.mtime))))
    print("Last Accessed: {}".format(time.strftime('%Y-%m-%d %H:%M:%S',
                                     time.localtime(tag.file_info.atime))))
    print("# {}".format('=' * 78))


def duration_from_seconds(s):
    """Module to get the convert Seconds to a time like format."""
    s = s
    m, s = divmod(s, 60)
    h, m = divmod(m, 60)
    d, h = divmod(h, 24)
    timelapsed = "{:01d}:{:02d}:{:02d}:{:02d}".format(int(d),
                                                      int(h),
                                                      int(m),
                                                      int(s))
    return timelapsed

fileName = "1.mp3"
filePath = "{}\\1.mp3".format(pathlib.Path().absolute())

# ten_seconds = 10 * 1000

track_info(fileName)
# song = AudioSegment.from_mp3(fileName)
# first10Seconds = song[:ten_seconds]
# first10Seconds.export(fileName+'-sample.mp3', format="mp3")

import requests

#r = requests.get('http://127.0.0.1:8080/')

#print(r.status_code)
#print(r.headers)
#print(r.text)

url = 'http://127.0.0.1:8080/file/sample'
files = {
    'file': open('1.mp3', 'rb')
}

r = requests.post(url, files=files)
print(r.text)





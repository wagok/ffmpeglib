FFMPEG documentation

1. Синопсис

ffmpeg [global_options] {[input_file_options] -i ‘input_file’} ... {[output_file_options] ‘output_file’} …

2. Описание

ffmpeg это быстрый аудио и видео конвертер который также может брать видео с живых аудио/видео источников. Он также может конвертировать между любыми частотами дискретизации и изменять размер видео на лету с высоким качеством многофазных фильтров. FFmpeg считывает данные из произвольного количества входных «файлов» (которые могут быть обычные файлы, внутренние потоки, сетевых потоков, устройства захвата и т.д.), определяемое через опцию -i, и записывает в произвольное количество выходных «файлов», которая задаются простым именем файла вывода. Все, что указана в командной строке, что не является опцией - является выходным файлом. 
Каждый входной или выходной Файл может, в принципе, содержать любое количество потоков различных типов (видео / аудио / субтитров / присоединения / данные). Допустимое количество типов и / или  потоков может быть ограничена форматом контейнера. Выбор, какие потоки, из каких входов буду выдаваться в какие выходы осуществляется либо автоматически, либо с опцией -map (см. раздел Выбор потока).
Для ссылки на входные файлы в настройках, вы должны использовать их индексы (начиная с 0). Например, первый входного файл 0, второй 1 и т.д. Кроме того, потоки внутри файла называются по их индексы. Например 2:03 относится к четвертому потоку в третьем входном файле. Также см. главу поток спецификаторов.
Как правило порядок следования опций в строке имеет значение, некоторые опции могут присутствовать несколько раз. Каждое вхождение применяется к входному либо выходному файлу. Исключением из этого правила являются глобальные опции, которые должны быть определены в первую очередь.
 Не смешивайте входные и выходные файлы - сначала необходимо задать все входные файлы, потом  все выходные файлы. Также не смешивайте опции, которые принадлежат к разным файлам. Все опции применяются только к следующему входному или выходному файлу и сбрасываются между файлами.

Чтобы установить битрейт выходного файла 64Кбит/с:

ffmpeg -i input.avi -b:v 64k -bufsize 64k output.avi

Чтобы установить частоту кадров 24 кадра в секунду в выходном файле:

ffmpeg -i input.avi -r 24 output.avi

Чтобы установить частоту кадров входного файла (имеет смысл только для рав форматов) 1 кадр в секунду, а выходного 24 кадра в секунду:

ffmpeg -r 1 -i input.m2v -r 24 output.avi


Параметр формата может быть обязательным для входных рав форматов. 


3. Детальное описание

Перекодирования в FFmpeg для каждого выхода может быть описана в виде следующей схеме:

<input file> -> [demuxer] -> <encoded data packets> -> [decoder] -> <decoded frames> -> [encoder] -> <encoded data packets> -> [muxer] -> <output file>

FFmpeg вызывает (использует) библиотеку libavformat (содержащую демультиплексоры) для чтения входных файлов и получения пакетов, содержащих закодированные данные от них. При наличии нескольких входных файлов, FFmpeg пытается синхронизировать их, отслеживая самую раннюю метку времени на каждый активный входной поток.

Закодированные пакеты затем переправляются в декодер (если не установлена опция копирования). Декодер отдает уже несжатые кадры (рав видео / PCM аудио / ...), которые могут быть обработаны дальнейшей фильтрами (см. следующий раздел). После фильтрации кадры передаются в кодер, который кодирует их и выводит кодированные пакеты, которые передаются в мультиплексор, который записывает кодированные пакеты в выходной файл.

3.1 Фильтры 

Перед кодированием, FFmpeg может обрабатывать сырые аудио и видео кадры с использованием фильтров из библиотеки libavfilter. Если фильтров несколько, то они образуют граф фильтров. FFmpeg различает два типа filtergraphs: простые и сложные.

3.1.1 Простые графы фильтров

Простой filtergraphs такие, которые имеют только один вход и выход того же типа. В приведенной выше схеме они могут быть представлены просто если вставить дополнительный шаг между кодированием и декодированием:

<decoded frames> -> [simple filtergraphs] -> <filtered frames> -> [encoder] -> <encoded data packets> 

Простой граф фильтров настроен опцией -filter (-af audio filter aliase, -vf video filter aliase).
Простой фильтр может выглядить так:

input -> deinterlace -> scale -> fps -> output

Заметим, что некоторые фильтры изменяют свойства фрейма, но не содержание кадра. Например фильтр “кадров в секунду” в приведенном выше примере измененяет количество кадров, но не касаются содержания блока данных. Другим примером является setpts фильтр, который устанавливает только временные метки и передает кадры без изменений.

3.1.2 Сложные графы фильтров

Сложные графы фильтров являются такими, которые не могут быть описаны как линейная цепочка обработки, применимая к одному потоку.
Это тот случай, когда граф имеет более одного входа или выхода. 
Сложный граф фильтров может быть представлен следующей схемой:


input 0                                             output 0
input 1  -> complex filter graph -> 
input 2                                             output 1

Сложный граф фильтров настраивается опцией -filter_complex.
Эта опция глобальная, ьак как сложный фильтр не может по определению быть применен одиночному входному файлу. 

Опция ‘-lavfi’ эквивалентна ‘-filter_complex’

Тривиальный пример сложного графа фильтров является оверлей, который имеет два входящих потока и один выходдящий, где одно видео наложено на другое. Аудио аналог - фильтр amix.

3.2 Копирование потока

Копирование потока - это режим который задается опцией “-codec copy”. При этой опции ffmpeg не производит декодирование и кодирование, а выполняет только демультиплексирование и мультиплексирование. 
Это полезно для изменения формата контейнера или метаданных уровня контейнера. 
Процес показывает данная схема:

input file -> [demuxer] -> encoded data packets -> [muxer] -> output file

Так как тут нет декодирования и кодирования, то это происходит очень быстро и без потери качества. Однако, это может не сработать в некоторых случаях по ряду причин. 

4. Выбор потока

По у молчанию ffmpeg включает только только один поток каждого типа (аудио, видео, субтитры) представленные во входящем файле и добавляет их в каждый выходной файл. Он выбирает “лучший” поток, основываясь на следующих критериях: для видео, это поток с наибольшем разрешением, для видео, это поток большим количеством каналов, для субтитров, первый поток субтитров. В ситуации, когда несколько потоков одного типа имеют одинаковое качество, поток с меньшим индексом будет выбран. 

Вы можете отключить некоторые из этих умолчаний, используя опции -vn, -an, -sn.
Для полного ручного управления используйте опции -map, которые отключают умолчания.

5. Опции

Все числовые опции, если специально не указано обратное, допускают числовое представление, после которого может идти СИ префикс, например: “К”, “М”, “G”.
Если ‘i’ добавлена в префиксу, то полный префикс будет интерпретирован как двоичный, т.е. которые базируется на 1024 а не на 1000. 
Добавление “В” к префиксу умножает значение на 8. Это допускает использование, например: “KB”, “MiB”, “G” и “В” как суфик числа. 

Опции, которые не принимают аргументы являются булевыми и устанавливают соответствующее значение в true. Они могут быть установлены в значение flase путем добавления перед опцией “no”. Например, “-nofoo” установит булеву опцию foo в false.

5.1 Спецификаторы потока. 

Некоторые опции применяются для отдельного потока, например битрейт или кодек. Спецификаторы потока используются для того, чтобы точно указать каому потоку принадлежит данный спецификатор. 

Спецификатор потока это строка непосредственно добавленная к опции и разделенная от неё двоеточием. Например, “-codec:a:1 ac3” содержит спецификатор “а:1”, который указывает на второй аудио поток. Вся опция со спецификатором говорит применить кодек ac3 для второго аудио потока. 

Спецификатор может указывать сразу на несколько потоков и тогда применяется ко всем потокам. Например спецификатор “-b:a 128k” указывает на все аудио потоки. 
Пустой спецификатор потока подразумевает все потоки. Например -codec: copy укажет копировать все потоки без перекодирования. 
Возможные формы спецификаторов потока:

“stream_index” 
   
Указывает на поток по индексу. Например, “-threads:1 4”  установит количество потоков исполнения при обработке второго потока равным 4. 

‘stream_type[:stream_index]’

stream_type является одним из следующих: 'V' для видео, 'A' для аудио, 'S' для субтитров, "D" для передачи данных, и "Т" для вложений.
stream_index выбранного типа либо если не указано, то все потокам выбранного типа. 

‘p:program_id[:stream_index]’

Если stream_index задан, то он указывает на поток с индексом stream_index в программе с ID program_id. В противном случае она соответствует всем потокам в программе.

‘#stream_id’
Указывает на поток со специально заданным ID 


5.1 Общие опции

Эти параметры являются общими для инструментов FF *.

“-L” 
	Показать лицензию 

“-h, -?, -help, --help [arg]”

Показать справку. Необязательный параметр может быть указан для вывода помощи по специфическом вопросу. 

‘decoder=decoder_name’

Выводит подробную информацию о декодере по имени decoder_name. Используйте опцию '-decoders’, чтобы получить список всех декодеров.

‘encoder=encoder_name’

Распечатать подробную информацию о кодере по имени encoder_name. Используйте опцию '-encoders ", чтобы получить список всех кодеров.

‘demuxer=demuxer_name’, ‘muxer=muxer_name’, ‘filter=filter_name’

Распечатать подробную информацию о (демультиплексорах, мультиплексорах, фильтрах) по имени ХХХ_name. Используйте опцию '-ХХХs ", чтобы получить список всех демультиплексорах, мультиплексорах или фильтрах.

‘-version’

Показать  версию

‘-formats’

Показать доступные форматы
 
‘-codecs’

Показать все кодеки изветные libavcodec
Обратите внимание, что "кодек" - это термин используемый в данной документации в качестве того, что более правильно называть медиа формат битового потока.

‘-decoders’, ‘-encoders’, ‘-bsfs’, ‘-protocols’, ‘-filters’, ‘-pix_fmts’, ‘-sample_fmts’

То же, что и предыдущая опция для декодеров, кодеров, фильтров битового потока, протоколов, фильтров, форматов пикселей, форматов образцов. 

‘-layouts’

Отображать название канала и стандартных макетов канала.

‘-loglevel [repeat+]loglevel | -v [repeat+]loglevel’

Устанавливает уровень ведения журнала логирования. Добавление "repeat+" означает, что повторяющийся вывод не должен быть сжат до первой строки. Опция “repeat+” может использоваться без установления loglevel и тогда будет использоваться loglevel по умолчанию.

Допустимые опции loglevel:

quit
Не показывать вообще ничего.
panic
Показывать только критические ошибки приводящие к крэшу процесса. (Пока не используется)
fatal
Показывать только критические ошибки. Это такие ошибки после которых продолжение невозможно.
error
Показывать все ошибки включая те, после которых можно продолжить работу.
warning
Показывать все ошибки и предупреждения. 
info
Показывать информационные сообщения в процессе работы, а также все предупреждения и ошибки. 
verbose
То же что и info, но больше отладочной информации
debug
Показывать всё включая отладочную информацию

По умолчанию программа выводит лог в стандартный stderr, если окраска поддерживается терминалом, цвета используются для обозначения ошибок и предупреждений. Подстветка логов может быть отключена установка переменной окружения AV_LOG_FORCE_NOCOLOR или NO_COLOR, или может быть формирование установкой переменной AV_LOG_FORCE_COLOR. Использование переменной окружения NO_COLOR устарела и будет удалена в следующей версии FFmpeg.

-report 

Полный вывод коммандной строки и результата работы в файл с именем program-YYYYMMDD-HHMMSS.log в текущей директории. Этот файл может быть полезен для отчета об ошибках. Эта опци также подразумевает по умолчанию -loglevel verbose 
Установка переменной окружения FFREPORT в любое значение имеет такой же эффект. 
Если заначение переменной является последовательностью ключ=значение, разделенными двоеточием, то будут восприниматься как опции. Значение опций, содержащих специальные символы либо двоеточние, должны экранироваться esc-последовательностью. Допустимы следующие опции:
“file” 

устанавливает имя файла используемого для отчета; %p - вставить имя программы; %t - вставить метку времени; %% вставить %
Ошибка при разборе значения переменной не является критической ошибкой и не будет отображаться в отчете.

‘-cpuflags flags (global)’

Устанавливает и сбрасывает флаги ЦПУ. Эта опция предназначена для тестирования. Не используйте их если не знаете что делаете. 

ffmpeg -cpuflags -sse+mmx ...
ffmpeg -cpuflags mmx ...
ffmpeg -cpuflags 0 ...

Допустимые флаги для данной опции:

‘x86’
‘mmx’
‘mmxext’
‘sse’
‘sse2’
‘sse2slow’
‘sse3’
‘sse3slow’
‘ssse3’
‘atom’
‘sse4.1’
‘sse4.2’
‘avx’
‘xop’
‘fma4’
‘3dnow’
‘3dnowext’
‘cmov’
‘ARM’
‘armv5te’
‘armv6’
‘armv6t2’
‘vfp’
‘vfpv3’
‘neon’
‘PowerPC’
‘altivec’
‘Specific Processors’
‘pentium2’
‘pentium3’
‘pentium4’
‘k6’
‘k62’
‘athlon’
‘athlonxp’
‘k8’

‘-opencl_options options (global)’

Установка опций OpenCL. Эта опция допустима только в случай, если ffmpeg скомпилирован с ключом --enable-opencl
options должны быть списком ключ=значение разделенные двоеточием. Смотри раздел “OpenCL опции” в руководстве ffmpeg-utils по поддерживаемым опциям.

5.3 AVOptions 

Данные опции поддерживаются непосредственно библиотеками: libavformat, libavdevice и libavcodec. 
Чтобы увидеть список доступных опций, используй опцию “-help”. Опции разделены на два раздела:

‘generic’
Эти опции могут быть установленны для любого контаейнера, кодека либо устройства. Общие опции отображаются под опциями AVFormatContaxt для контйнеров/устройств и под опциями AVCodecContext для кодеков. 

‘private’
Эти опции специфичны для контейнера, устройства либо кодека. Частные опции отображаются под соответствующими контейнерами, устройствами, кодеками. 
Например, чтобы добавить ID3v2.3 заголовок, вместо заголовка по умолчанию ID3v2.4 в MP3 файле, используй частную опцию “id3v2_version” MP3 мультиплексора. 

ffmpeg -i input.flac -id3v2_version 3 out.mp3

Все AVOptions кодека указываются для конкретного потока.

Синтаксис “-nooption” не может быть использован для булевых опций. Необходимо использовать синтаксис “-option 1” or “-option 0”

Старый недокументированный способ указания потока в опциях AVOptions путем добавления префикса v/a/s к имени опции является устаревшим и будет впоследствии уделен.

5.4 Основные опции

‘-f fmt (input/output)’

Указание формата входного либо выходного файла. Формат обычно определяется автоматически для входных фалов и предполагается формат выходного файла исходя из расширения файла. Таким образом данная опция не нужна в большинстве случаев. 

‘-i filename (input)’
 
Имя входного файла

-y (global)’

Перезаписывать выходной файл без дополнительных вопросов. 

‘-n (global)’

Не перезаписывать выходные файлы и прекращать работу немедленно если заданный выходной файл существует. 

‘-c[:stream_specifier] codec (input/output,per-stream)’
‘-codec[:stream_specifier] codec (input/output,per-stream)’

Данные опции позволяют выбрать энкодер (кода используется перед выходным файлом) или декодер (когда задается перед входным файлом) для одного и более потоков. 
Кодек это имя кодера/декодера либо специальное значение “copy” (только для выходных файлов), которое показывает, что потк не должен быть перекодирован. 
Например:

ffmpeg -i INPUT -map 0 -c:v libx264 -c:a copy OUTPUT

Кодировать все видео потоки через libx264 и копировать все аудио потоки. 

‘-t duration (output)’
Прекратить запись вывода после того как его продолжительность достигнет указанную продолжительность. Продолжительность может быть указана числом в секундах либо ф формате hh:mm:ss[.xxx]
Опции -to и -t являются взаимоисключающими и -t имеет приоритет.  

‘-to position (output)’
Прекоатить вывод в позиции. Позиция может указана как число в секундах или формате hh:mm:ss[.xxx].
Опции -to и -t являются взаимоисключающими и -t имеет приоритет.  

‘-fs limit_size (output)’
Установить лимит размера файла в байтах. 

-ss position (input/output)’

Когда используется как опция для входного файла (перед -i), те “перематывается” в указанную позицию входящего файла. 
Необходимо заметить, что в большинстве форматов нельзя переместиться точно в заданную позицию, и тогда ffmpeg перемещается (перематывает) в ближайшую точку перемотки до указанной позиции. 
В случае транскодинга и когда включена опция “-accurate_seek” (по умолчанию), этот дополнительный отрезок между точкой перемотки и указанной позицией перемотки будет декоодирован и отброшен. 
Если происходит копирование потока либо когда указано опция ‘-noaccurate_seek’ этот дополнительный отрезок останется. 
    Если опция стоит как опция вывода (перед файлом вывода), то декодирование входного файла будет происходить, но результат будет отбрасываться до тех пор, пока не дойдет до отметки указанной как position.
Позиция может указана как число в секундах или формате hh:mm:ss[.xxx].
‘-itsoffset offset (input)’

Устанавливает входное смещение в секундах. [-]hh:mm:ss[.xxx] синтакс также допустим.  Смещение добавляется к метке времени входного файла. Указание положительного смещения означает, что соответствующий поток задержан на указанное смещение. ‘-timestamp time (output)’
Устанавливает отметку времени записи контейнера. Синтаксис времени следующий:

now|([(YYYY-MM-DD|YYYYMMDD)[T|t| ]]((HH:MM:SS[.m...])|(HHMMSS[.m...]))[Z|z])

Если установлено значение now, то будет установлено текущее время. Время указывается в локале пользователя если не добавлен ‘z’ или ‘Z’, в этом случае время интерпритируется как UTC. Если год-месяц-день не указаны, то берется текущий год-месяц-день. 

‘-metadata[:metadata_specifier] key=value (output,per-metadata)’

Устанавливает метаданные, в формате ключ=значение. 

Дополнительный спецификатор метаданных может устанавливаеться на поток либо раздел. Смотри -map_metadata опцию. 
Данная опция переопределяет метаданные установленную опцией -map_metadata. Также возможно удалить метеданные передав пустое значение.
Например, для установления заголовка в выходном файле:
ffmpeg -i in.avi -metadata title="my title" out.flv
Чтобы язык установить первого аудиопотока:
ffmpeg -i INPUT -metadata:s:a:1 language=eng OUTPUT


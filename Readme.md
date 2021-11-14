Актуальная версия СКЗИ находится в ветке **master**.

Текущая стадия paszi: консольное приложение.

Используемые библиотеки:
   * javafx.base;
   * javafx.controls;
   * javafx.fxml;
   * java.desktop;
   * java.sql;
   * jcommander;
   * com.google.common;

```
Usage: <main class> [options]
  Options:
    -b, --bsize
      Block size(16,32)
      Default: 16
  * -i, --input
      Input file location
  * -l, --login
      Login
  * -m, --mode
      Mode(encrypt,decrypt)
    -o, --output
      Output file location
      Default: D:\Users\Alex\IdeaProjects\kripto (current program folder)
  * -p, --pass
      Password. Must be 10+ symbols of Cyrillic,Latin and arithmetic operands. 
      Mustn't consists only from login's characters.
    -r, --rounds
      Rounds number(1-255)
      Default: 2
```

Задание
Напишите программу на языке программирования java, которая прочитает файл tickets.json и рассчитает:
- Минимальное время полета между городами Владивосток и Тель-Авив для каждого авиаперевозчика
- Разницу между средней ценой  и медианой для полета между городами  Владивосток и Тель-Авив

Программа должна вызываться из командной строки Linux, результаты должны быть представлены в текстовом виде. 
В качестве результата нужно прислать ответы на поставленные вопросы и ссылку на исходный код.

## Запуск
### Прописать в properties путь к Json файлу
### На Linux
```java -jar jsonTask.jar```
```powershell
Minimum flight time between the cities of Vladivostok and Tel Aviv for each air carrier:
TK: 05:50:00
S7: 06:30:00
SU: 06:00:00
BA: 08:05:00
Average price: 13960.0
Median price: 13500.0
Difference: 460.0
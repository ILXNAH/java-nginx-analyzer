Zadani DEV ulohy:
=================

Mate k dispozici java kod (vcetne gradle buildu) ktery nacita access logy a pocita z nich metriky. 
Po spusteni provede nacteni access logu apache/nginxu a vystavi server s metrikami, vypoctenymi z
access logu na adrese http://localhost:9400/metrics.

Priklad spusteni aplikace:
```bash
./gradlew build
java -cp "build/libs/*" Main
```

priklad metrik:
```bash
wget -q  -O - http://localhost:9400/metrics |grep nginxlog_status_codes_total
# HELP nginxlog_status_codes_total Total number of status codes from nginx log
# TYPE nginxlog_status_codes_total counter
nginxlog_status_codes_total{status="200"} 56941.0
nginxlog_status_codes_total{status="206"} 693.0
nginxlog_status_codes_total{status="301"} 7107.0
nginxlog_status_codes_total{status="302"} 445.0
nginxlog_status_codes_total{status="304"} 5793.0
nginxlog_status_codes_total{status="400"} 60.0
nginxlog_status_codes_total{status="403"} 4.0
nginxlog_status_codes_total{status="404"} 1847.0
nginxlog_status_codes_total{status="405"} 13.0
nginxlog_status_codes_total{status="409"} 1.0
nginxlog_status_codes_total{status="410"} 2.0
nginxlog_status_codes_total{status="418"} 24.0
nginxlog_status_codes_total{status="421"} 1.0
nginxlog_status_codes_total{status="429"} 2994.0
nginxlog_status_codes_total{status="500"} 6926.0
```


Ukolem je nasledujici:
======================
1. upravit kod tak, aby nepocital zvlast pocet radek s kodem `400`, `403`, `404`, ale aby je pocital dohromady 
   K dispozici tedy budou zaznamu s kodem `2xx`, `3xx`, `4xx`, `5xx` 
   (= dohromady vsechny dvoustovky, tristovky, ctyrstovky, petistovky)
2. zabaleni do docker image (vcetne kompilace a spusteni). tj vytvoreni Dockerfile, ktery provede:
   * instalaci java SDK (napr. temurin)
   * kompilaci java aplikace
   * spusteni java aplikace
   * nakonfigurovani docker image tak aby bylo jasne na kterem portu posloucha aplikace (`EXPOSE`)


Co dodat:
=========
Zabaleny git repozitar (tj vcetne adresare `.git`) ve kterem bude README, java kod (upraveny), 
Dockerfile, a vse potrebne pro kompilaci a spusteni aplikace.
V README bude popsano jak docker image vytvorit a spustit 
a jak se dostat k metrikam ktere bezi na `http://localhost:9400/metrics`


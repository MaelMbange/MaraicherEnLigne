.SILENT:

ALL:	./exe/CreationBD ./exe/Client ./exe/serveur ./exe/client_t

./exe/serveur:	./src/serveur/serveur.cpp ./src/lib/mynet.cpp ./src/lib/protocol.hpp
			echo -e "\033[92mCreation serveur ...\033[0m"
			g++ --std=c++2a -o ./exe/serveur ./src/serveur/serveur.cpp ./src/lib/mynet.cpp -lpthread -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

./exe/CreationBD:	./src/DataBase/CreationBD.cpp
			echo -e "\033[92mCreation CreationBD ...\033[0m"
			g++ -o ./exe/CreationBD ./src/DataBase/CreationBD.cpp -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

./obj/mainclient.o:	./src/Client/mainclient.cpp
			echo -e "\033[94mCreation mainclient.o...\033[0m"
			g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o ./obj/mainclient.o ./src/Client/mainclient.cpp

./obj/windowclient.o:	./src/Client/windowclient.cpp ./src/lib/mynet.cpp
			echo -e "\033[94mCreation windowclient.o...\033[0m"
			g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o ./obj/windowclient.o ./src/Client/windowclient.cpp

./obj/moc_windowclient.o:	./src/Client/moc_windowclient.cpp
			echo -e "\033[94mCreation moc_windowclient.o...\033[0m"
			g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o ./obj/moc_windowclient.o ./src/Client/moc_windowclient.cpp

./exe/Client:	./obj/mainclient.o ./obj/windowclient.o ./obj/moc_windowclient.o
			echo -e "\033[92mCreation Client...\033[0m"
			g++ -Wno-unused-parameter -o ./exe/Client ./src/lib/mynet.cpp ./obj/mainclient.o ./obj/windowclient.o ./obj/moc_windowclient.o  /usr/lib64/libQt5Widgets.so /usr/lib64/libQt5Gui.so /usr/lib64/libQt5Core.so /usr/lib64/libGL.so -lpthread -I/usr/include/mysql -m64 -L/usr/lib64/mysql -lmysqlclient -lpthread -lz -lm -lrt -lssl -lcrypto -ldl

./exe/client_t:	./src/Client/Client_terminal.cpp ./src/lib/mynet.cpp
			echo -e "\033[92mCreation client_terminal...\033[0m"
			g++ -o ./exe/client_t ./src/Client/Client_terminal.cpp ./src/lib/mynet.cpp


clean:
			rm obj/*.o exe/*

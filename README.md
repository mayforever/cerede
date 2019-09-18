# CeReDe
## Centralized Remote Desktop

**CeReDe** is opensource remote desktop. it`s like _**Team Viewer**_, _**AnyDesk**_, the different is you need costumize and provide your own server. base in what you need.

**ceredeserver** is the serverside of this project. it handled username, password, address, and command of every client.

Requirements :
* optional: port forwading certain ip. if you want to use it in wan
* configure **"conf/ceredeserver.conf.xml"**. 
	* **filePath** - the path of project.
	* **processorCount** - how many thread waiting for process.
	* **port** - port you use.
	* **address** - the ip of your server

Sample:

```
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
"http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <bean id = "ceredeserver" class = "com.mayforever.ceredeserver.conf.Configuration">
        <property name="filePath" value = "/home/mis/eclipse-workspace/ceredeserver/"/>
	<property name="processorCount" value = "1"/>
	<property name="port" value = "4452"/>
	<property name="address" value = "0.0.0.0"/>

    </bean>
</beans>

```

**ceredeclient** this is the client side that comunicate to the server. this need to install in every pc we want to control or use to control. 

Requirement
* configure **"conf/ceredeserver.conf.xml"**. 
	* **filePath** - the path of project.
	* **chunkCount** - I encounter error in bytebuffer in different os. it depends upon so I sudgest, chunkCount => 5.
	* **serverPort** - the port of the server.
	* **serverAddress** - the ip of your server.
	* **username** - it must be unique in every client that connect to server.
	* **password** - password
```

<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
"http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <bean id = "ceredeclient" class = "com.mayforever.ceredeclient.conf.Configuration">
        <property name="filePath" value = "/home/mis/NetBeansProjects/ceredeclient/"/>
	<property name="chunkCount" value = "10"/>
	<property name="serverPort" value = "4452"/>
	<property name="serverAddress" value = "192.168.0.23"/>
	<property name="username" value = "username"/>
	<property name="password" value = "password"/>
    </bean>
</beans>

```

for now. it support viewing and controlling other pc. it not support clipboard and filetransfer, but next time I add on it. I add those both feature. 

Sorry for my bad english. I hope you understand, thanks.

ps: this is not final. I need to improve it. so I open in any suggestion. 

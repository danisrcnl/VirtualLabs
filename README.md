# Applicazioni Internet 2020 - Project
## @Politecnico di Torino
### Daniele Sarcinella (danisrcnl) and Matteo Pellegrino (mattellegrino)

You can either clone the entire repository or just get the *docker-compose.yml* file it contains. The only prerequisite is that you have **docker** installed on your machine.

## Deploy instructions

Put the "docker-compose.yml" file in a custom position in your filesystem

```bash
cp docker-compose.yml /home/username/myfolder
```

Open the terminal and change directory until you're in the same folder where you put "docker-compose.yml" previously

```bash
cd /home/username/myfolder
```

Launch the command "docker-compose up"

```bash
docker-compose up
```

The 3 images (server, client and db) will be pulled from the repository and, at the end of the process, they will be compiled. Once finished, type http://localhost:4200 on your browser to start using the application.

## Usage instructions:

Database is initialized with some data:

- **users** ["username":"password" (role)]: 

		"s000000@studenti.polito.it":"password" 	(student)
		"s000001@studenti.polito.it":"password" 	(student)		
		"s000002@studenti.polito.it":"password" 	(student)
		"s000003@studenti.polito.it":"password" 	(student)
		"s000004@studenti.polito.it":"password" 	(student)
		"s000005@studenti.polito.it":"password" 	(student)
		"s000006@studenti.polito.it":"password" 	(student)
		"s000007@studenti.polito.it":"password" 	(student)

		"d000000@polito.it":"password" 		(teacher)
		"d000001@polito.it":"password" 		(teacher)


- **courses**:

		"Applicazioni Internet"


- **courses-teachers relationships** [courseName":"username"]:

		"Applicazioni Internet":"d000000@polito.it"

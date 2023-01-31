# nwt-kts-back
Backend for NWT &amp; KTS uni project

## Setup

Add Google Maps API key to `application.properties`
```properties
google.maps.api.key=API_KEY
```

Use docker command in terminal to start database:
```sh
docker-compose up
```
Note: When you are finished, run `docker-compose down`.

Run backend with an IDE of your choice. We recommend IntelliJ.


## Simulation
A script is provided for simulating rides in real time.
### Requirements:
- Google Maps API key
- Python
- locust and googlemaps python modules

Create a new file in the root of this repo called `maps_api.key` and paste the key into it (w/o new line).

Type this command in terminal to install dependencies:
```sh
pip install locust googlemaps
```
Use of virtual environment is recommended.

Creating a virtual environment in Linux and macOS:
```shell
   python3 -m venv venv
   source venv/bin/activate
```

Creating a virtual environment in Windows:
```shell
  python -m venv venv
  venv\Scripts\activate
```

### Running driver simulation
To run the simulation and see results follow these steps:
1. Start database with `docker-compose`
2. Run backend
3. Run [frontend](https://github.com/therealtgd/nwt-kts-front) with `ng serve -o` (`-o` is for opening the browser automatically)
4. Run simulation with following command
   ```sh
    locust --headless -u 3 -r 1 --run-time 15m
   ```
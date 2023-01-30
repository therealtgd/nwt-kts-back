from dataclasses import dataclass
from random import randrange
from typing import Any

import googlemaps
from locust import HttpUser, between, task


@dataclass
class LatLng:
    lat: float
    lng: float

    @classmethod
    def fromJson(cls, latLng):
        return cls(
            latLng['lat'],
            latLng['lng'],
        )

    @property
    def tuple(self):
        return (self.lat, self.lng)

@dataclass
class Vehicle:
    id: int
    licencePlate: str
    capacity: int
    petsAllowed: bool
    babiesAllowed: bool
    position: LatLng

    @classmethod
    def fromJson(cls, vehicle):
        return cls(
            vehicle['id'],
            vehicle['licencePlate'],
            vehicle['capacity'],
            vehicle['petsAllowed'],
            vehicle['babiesAllowed'],
            LatLng.fromJson(vehicle['position']),
        )

@dataclass
class Driver:
    id: int
    username: str
    email: str
    displayName: str
    enabled: bool
    status: str
    vehicle: Vehicle

    @classmethod
    def fromJson(cls, driver):
        return cls(
            driver['id'],
            driver['username'],
            driver['email'],
            driver['displayName'],
            driver['enabled'],
            driver['status'],
            Vehicle.fromJson(driver['vehicle']),
        )

@dataclass
class UserCreds:
    email: str
    password: str

@dataclass
class ApiResponse:
    success: bool
    status: int
    message: str
    body: Any

class Globals:
    GOOGLE_MAPS_API_KEY = None
    users_creds = [UserCreds(f"driver{i}@gmail.com", "driver") for i in range(1,4)]
    
    locations = [
        LatLng(45.235866, 19.807387),     # Djordja Mikeša 2
        LatLng(45.247309, 19.796717),     # Andje Rankovic 2
        LatLng(45.259711, 19.809787),     # Veselina Maslese 62
        LatLng(45.261421, 19.823026),     # Jovana Hranilovica 2
        LatLng(45.265435, 19.847805),     # Bele njive 24
        LatLng(45.255521, 19.845071),     # Njegoseva 2
        LatLng(45.249241, 19.852152),     # Stevana Musica 20
        LatLng(45.242509, 19.844632),     # Boska Buhe 10A
        LatLng(45.254366, 19.861088),     # Strosmajerova 2
        LatLng(45.223481, 19.847990),     # Gajeva 2
    ]
    taxi_stops = [
        LatLng(45.238548, 19.848225),   # Stajaliste na keju
        LatLng(45.243097, 19.836284),   # Stajaliste kod limanske pijace
        LatLng(45.256863, 19.844129),   # Stajaliste kod trifkovicevog trga
        LatLng(45.255055, 19.810161),   # Stajaliste na telepu
        LatLng(45.246540, 19.849282),   # Stajaliste kod velike menze
    ]


with open('maps_api.key', 'r') as f:
    Globals.GOOGLE_MAPS_API_KEY = f.read()

gmaps = googlemaps.Client(key=Globals.GOOGLE_MAPS_API_KEY)

class QuickstartUser(HttpUser):
    host = 'http://localhost:8080'
    wait_time = between(0.5, 2)

    def on_start(self):
        # Login
        token = self.client.post('/auth/signin', json=Globals.users_creds.pop(0).__dict__).json()
        self.auth_header = {
            'Authorization': f'Bearer {token["message"]}',
            'Content-Type': 'application/json',
        }

        driver_json = self.client.get('/driver', headers=self.auth_header).json()
        self.driver = Driver.fromJson(driver_json['body'])

        self.driving_to_start_point = True
        self.driving_the_route = False
        self.driving_to_taxi_stop = False

        self.departure = self.driver.vehicle.position
        self.destination = Globals.locations.pop(randrange(0, len(Globals.locations)))
        
        self.waypoints: list[LatLng] = []
        self.get_new_coordinates()

    @task
    def update_vehicle_coordinates(self):
        if len(self.waypoints) > 0:            
            self.client.put(
                f"/vehicle/update/{self.driver.vehicle.id}/position",
                json=self.waypoints.pop(0).__dict__,
                headers=self.auth_header
            )
        else:
            if self.driving_to_start_point:
                self.driving_to_start_point = False

                self.departure = self.destination
                while (self.departure is self.destination):
                    self.destination = Globals.locations.pop(randrange(0, len(Globals.locations)))
                
                self.get_new_coordinates()
                self.driving_the_route = True

            elif self.driving_the_route:
                self.driving_the_route = False

                Globals.locations.append(self.departure)
                self.departure = self.destination
                self.destination = Globals.taxi_stops[randrange(0, len(Globals.taxi_stops))]
                
                self.get_new_coordinates()
                self.driving_to_taxi_stop = True

            elif self.driving_to_taxi_stop:
                self.driving_to_taxi_stop = False
 
                Globals.locations.append(self.departure)
                self.departure = Globals.taxi_stops[randrange(0, len(Globals.taxi_stops))]
                self.destination = Globals.locations.pop(randrange(0, len(Globals.locations)))

                self.get_new_coordinates()
                self.driving_to_start_point = True

    def get_new_coordinates(self):
        directions = gmaps.directions(self.departure.tuple, self.destination.tuple)
        for leg in directions[0]['legs']:
            for step in leg['steps']:
                for point in googlemaps.convert.decode_polyline(step['polyline']['points']):
                    self.waypoints.append(LatLng.fromJson(point))

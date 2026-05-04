package services;

import aircrafts.Aircraft;

public interface IGroundService {

	// Returns true if is servicable
    boolean canService(Aircraft aircraft);

	// Serve the flight
    void serviceFlight(Aircraft aircraft);
}
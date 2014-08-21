/* Events */

// when battle starts.
+onStartBattle : true <- !startMyBattlePlan.

// if some bullet hits me, take cover!!!
+onHitByBullet(I) : true <- .print("I took a shot"); !takeCover.

// if some tank hits me, move ahead or back 200 mts.
+onHitTank(T, E) : movingAhead(10)  <- +movingBack; dropAccelerator; back(200); .wait("+actionFinished"); -movingAhead(10); -movingBack.
+onHitTank(T, E) : not movingAhead(X) <- +movingAhead(0); ahead(200).
+onHitTank(T, E) : not movingBack & movingAhead(X) & X < 10 <- ?movingAhead(X); -movingAhead(X); .print(X); +movingAhead(X+1); ahead(200); .wait("+actionFinished"); -movingAhead(X+1).

+onDeath : true <- .print("I'm dead :( looking for a new boss").

/* Plans */

// my initial battle plan
+!startMyBattlePlan : true <- .print("battle has started, let's fight!"); 
							  ahead(910); .wait("+actionFinished");
							  turnLeft(70, 1); .wait("+actionFinished");
							  ahead(1300).
							  
// find a "safe" place in the battle field.
+!takeCover : true <- .print("Ouchh, I'll find a safe place...");
					  back(50); .wait("+actionFinished");
					  turnLeft(45, -1); .wait("+actionFinished");
					  ahead(200).

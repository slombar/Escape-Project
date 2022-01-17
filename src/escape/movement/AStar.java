package escape.movement;

import escape.board.Board;
import escape.board.piece.PieceType;
import escape.required.EscapePiece;
import escape.required.EscapePiece.*;
import escape.board.coordinate.Coordinate;
import escape.required.LocationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class AStar {

    //vars
    private PieceName name;
    private Board board;
    private HashMap<PieceName, PieceType> pieceTypes;
    private ArrayList<Coordinate> queue;
    private ArrayList<Coordinate> visited;
    private HashMap<Coordinate, Integer>G;
    private MovementRule rule;
    private Coordinate from;
    private Coordinate to;
    private boolean unblock;

    /**Constructor for the AStar class
     *
     * @param name
     * @param board
     * @param pieceTypes
     */
    public AStar(EscapePiece.PieceName name, Board board, HashMap<PieceName, PieceType> pieceTypes) {
        this.name = name;
        this.board = board;
        this.pieceTypes = pieceTypes;
        this.queue = new ArrayList<Coordinate>();
        this.visited = new ArrayList<Coordinate>();
        this.G = new HashMap<Coordinate, Integer>();
        this.rule = new MovementRule(this.pieceTypes.get(name).getMovementPattern(), board);
    }

    /**
     * Find the path between two coordinates if it exists
     * @param f from coordinate
     * @param t to coordinate
     * @return
     */
    public ArrayList<Coordinate> findPath(Coordinate f, Coordinate t) {
        this.from = f;
        this.to = t;

        //if the movement pattern is linear but the from and to aren't, can't find path
        if(pieceTypes.get(name).getMovementPattern() == MovementPattern.LINEAR && !board.isLinear(f, t)) {
            return null;
        }
        //if the target is a block, can't find path
        if(board.getLocationTypeAt(t) == LocationType.BLOCK) {
            return null;
        }

        //if coordinates are the same
        if(f.equals(t)) {
            return null;
        }

        //if the current piece has the unblock attribute, then set unblock to true. otherwise it is false
        unblock = pieceTypes.get(name).getAttributes().get(PieceAttributeID.UNBLOCK) != null;

        //get the maximum distance for a given piece based on its assigned value
        int maxDistance = pieceTypes.get(name).getAttributes().get(PieceAttributeID.DISTANCE).getValue();

        // map of previous coordinates and list of valid neighbors
        HashMap<Coordinate, Coordinate> currentPath = new HashMap<Coordinate, Coordinate>();
        ArrayList<Coordinate> validNeighbors = new ArrayList<Coordinate>();

        //add the first coordinate to the queue
        queue.add(from);
        G.put(from, 0);

        //while the queue isn't empty
        while(queue.size() != 0) {
            //sort queue by distance
            Collections.sort(queue, new Comparator<Coordinate>() {
                /**
                 * Overwritten compare method that uses distance to for coordinate comparison
                 * @param c1
                 * @param c2
                 * @return 0 if equal, less than 0 if the value of c1 < c2, and greater than 0 if the value of c1>c2
                 */
                @Override
                public int compare(Coordinate c1, Coordinate c2) {
                    //first and second values via distanceto function
                    Integer first = c1.distanceTo(to);
                    Integer second = c2.distanceTo(to);
                    //comparison here
                    return first.compareTo(second);
                }
            });
            // get the current coordinate (first coordinate in the queue)
            Coordinate current = queue.get(0);

            //if we arrive at the target
            if(current.equals(to)) {
                //get the path to return
                ArrayList<Coordinate> path = backTrace(current, currentPath);

                if(path == null) {
                    return null;
                }
                //if the path isn't value for the piece's distance attribute
                if(path.size() > maxDistance) {
                    return null;
                }
                return path;
            }

            //remove current node and add to previous queue
            queue.remove(current);
            visited.add(current);

            //target is unreachable, we are done
            if(isTargetUnreachable()) {
                return null;
            }

            //find valid neighbors for a given coordinate
            validNeighbors = findAllValidNeighbors(current);

            //add the coordinates and assign maximum distance to help order queue
            for(Coordinate neighbor : validNeighbors) {
                //value for current neighbor is total distance to to
                G.putIfAbsent(neighbor, 99999);

                //if we have not already seen this neighbor
                if(!visited.contains(neighbor)) {
                    //add it
                    currentPath.put(neighbor, current);
                    //add to the score of the current coordinate
                    int newGScore = G.get(current) + 1;

                    //if there is a piece at the given neighbor
                    if(board.getPieceAt(neighbor) != null) {
                        //find the next clear neighbor if possible
                        boolean clearNext = neighbor.equals(to) || findUnblockedLinearClearNeighbor(neighbor, current);
                        //if there is no clear neighbor next
                        if(!clearNext) {
                            //if the movement pattern of the curent piece is linear, return null
                            if(pieceTypes.get(name).getMovementPattern() == MovementPattern.LINEAR) {
                                return null;
                            }
                            //add next neighbor value
                            G.put(neighbor, -1);
                            //go to next neighbor
                            continue;
                        }
                    }
                    //if the queue contains the current neighbor
                    if(queue.contains(neighbor)) {
                        //compare the score with the neighbor's neighbor and if not -1
                        if(newGScore < G.get(neighbor) && G.get(neighbor) != -1) {
                            //add new score to G
                            G.put(neighbor, newGScore);
                            currentPath.put(neighbor, current);
                        }
                    //if the old list doesn't contain the neighbor and the G value is not -1 (new coordinate)
                    } else if (!visited.contains(neighbor) && G.get(neighbor) != -1) {
                        //add new neighbor to the queue
                        queue.add(neighbor);
                    }
                }
            }
        }
        //there was no path
        return null;
    }

    /**
     * Find all the valid neighbors for a given coordinate
     * @param current the coordinate you are checking
     * @return
     */
    ArrayList<Coordinate> findAllValidNeighbors(Coordinate current) {
        //get list of current neighbors
        ArrayList<Coordinate> neighbors = current.getNeighbors();
        //remove neighbors that are invalid
        neighbors.removeIf(n-> !board.isCoordinateValid(n));
        //remove neighbors that have pieces on them
        neighbors.removeIf(n-> board.getPieceAt(n) != null);
        //remove neighbors that are not a valid move type
        neighbors.removeIf(n-> (!rule.isValidMoveType(current, n, from, to)));
        //remove neighbors that are an exit and do not equal the 'to' coordinate
        neighbors.removeIf(n -> board.getLocationTypeAt(n) == LocationType.EXIT && !n.equals(to));
        //remove neighbors that are on a block location if the current piece doesn't have unblock attribute
        neighbors.removeIf(n -> board.getLocationTypeAt(n) == LocationType.BLOCK && !unblock);

        return neighbors;
    }

    /**
     * Find valid neighbor in linear pattern for ideal path
     * @param neighbor
     * @param current
     * @return
     */
    public boolean findUnblockedLinearClearNeighbor(Coordinate neighbor, Coordinate current) {
        boolean answer = false;
        //the list of neighbors
        ArrayList<Coordinate> neighbors = findAllValidNeighbors(neighbor);

        //remove non-linear neighbors
        neighbors.removeIf(n -> !board.isLinear(n, current));

        //if x values of the current neighbor and current coordinate are equal, remove any neighbors that do not have the same x value
        if(current.getX() == neighbor.getX()) {
            neighbors.removeIf(n -> n.getX() != current.getX());
        }
        //if y values of the current neighbor and current coordinate are equal, remove any neighbors that do not have the same y value
        if(current.getY() == neighbor.getY()) {
            neighbors.removeIf(n -> n.getY() != current.getY());
        }
        //if the two coordinates are diagonal, remove any neighbors that are not diagonal
        if(board.isDiagonal(neighbor, current)) {
            neighbors.removeIf(n -> !board.isDiagonal(n, current));
        }

        //remove any neighbors that have a piece on them and aren't the destination location
        neighbors.removeIf(n-> board.getPieceAt(n) != null && !n.equals(to));

        //remove neighbor if equal to current
        neighbors.removeIf(n -> n.equals(current));

        //if there are neighbors (not empty)
        if(neighbors.size() != 0) {
            //set x to true if unblock is true or the location at the given neighbor is not a block location
            boolean x = board.getLocationTypeAt(neighbors.get(0)) != LocationType.BLOCK || unblock;
            //x must be true for answer to be true
            //set answer to be true if the neighbor in the first position is null
            //set answer to be true if the neighbor in the first position is equals 'to' (the coordinate destination)
            answer = (board.getPieceAt(neighbors.get(0)) == null || neighbors.get(0).equals(to)) && x;
        }

        //the first neighbor isn't null or equals the final destination
        if(answer) {
            //neighbors of the neighbor
            ArrayList<Coordinate> neighbors2 = findAllValidNeighbors(neighbor);

            //check neighbors of neighbor
            for(Coordinate n : neighbors2) {
                //add to G if not equal to current and not included in current neighbors list
                if(!neighbors.contains(n) && !n.equals(current)) {
                    G.put(n, -1);
                }
            }
        }
        return answer;
    }

    /**
     * Back trace to get the path
     * @param current
     * @param previous
     * @return
     */
    public ArrayList<Coordinate> backTrace(Coordinate current, HashMap<Coordinate, Coordinate> previous) {
        //initialize path
        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(current);

        //if the current is null return null
        if(previous.get(current) == null) {
            return null;
        }
        //current isn't null
        else {
            //while we haven't reached the destination, continue on down the path until we reach it, adding parents as we go
            while (current != from) {
                //find the parent of the given coordinate
                Coordinate parent = previous.get(current);
                //if the parent is not where we came from, add it to the path
                if(parent != from) {
                    path.add(parent);
                }
                //set the current to be parent, effectively backtracing
                current = parent;
            }
            return path;
        }
    }

    /**
     * Determine if the target can be reached or not
     * @return true if reachable, false otherwise
     */
    public boolean isTargetUnreachable() {
        //default false return
        boolean answer = false;
        //list of neighbors for our destination location
        ArrayList<Coordinate> toNeighbors = findAllValidNeighbors(to);
        //if there are no neighbors, the target is unreachable.
        if(toNeighbors.size() == 0) {
            answer = true;
        //otherwise, if there are neighbors
        } else {
            //check all neighbors
            int done = 0;
            for (Coordinate n : toNeighbors) {
                if(G.get(n) != null && G.get(n) == -1) {
                    done++;
                }
            }
            //have we checked every neighbor?
            if(done == toNeighbors.size()) {
                answer = true;
            }
        }
        return answer;
    }


}

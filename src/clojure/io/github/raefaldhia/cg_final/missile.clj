(ns clojure.io.github.raefaldhia.cg_final.missile
    (:require [clojure.io.github.raefaldhia.cg_final.world :as World]
              [clojure.io.github.raefaldhia.cg_final.map_entry :refer [assocme getme updateme]]
              [clojure.io.github.raefaldhia.cg_final.world.object :as Object]))

(declare -check-intersection
         -create
         -render-fn
         -update-collision
         -update-fn
         -update-position)

(defn- -check-intersection [x y]
       (let [matrix (map vector x y) 
             -calc (fn [x y]
                       (->> (map #(-> (apply x %)
                                      (Math/pow 2.0)) y)
                            (apply +)))]
            (<= (-calc - (take 3 matrix))
                (-calc + (list (last matrix))))))

(defn -create
     [{:keys [position target] :as missile}]
     (-> (Object/-create missile)
         (assocme :collision-radius 0.2)
         (cond-> (nil? position)     (assocme :position [0.0 0.0 0.0])
                 (not (nil? target)) (assocme :velocity (mapv - target position)))
         (updateme :render-fns conj -render-fn)
         (updateme :update-fns conj -update-fn)))

(defn- -render-fn [bullet _world gl glu]
       (apply (memfn glTranslatef x y z) gl (getme bullet :position))
       (.glPushMatrix gl)
       (let [quadric (.gluNewQuadric glu)]
            (.gluSphere glu quadric 0.2 8 8))
       (.glPopMatrix gl))

(defn- -update-collision [bullet world _events _dt]
       (loop [bullet-final bullet
              world world
              [object & objects] (:objects world)]
             (if (nil? object)  
                 (vector bullet-final world)
                 (if (and (not (nil? (getme object :position)))
                          (-check-intersection (conj (getme bullet :position)
                                                     (getme bullet :collision-radius)) 
                                               (conj (getme object :position)
                                                     (getme object :collision-radius))))
                      (recur nil (update world :objects dissoc (key object)) objects)
                      (recur bullet-final world objects)))))

(defn- -update-fn [missile world events dt]
      (let [[missile world] (-update-collision missile world events dt)]
           (if (nil? missile)
               (vector missile world)
               (-update-position missile world events dt))))

(defn- -update-position [missile world _events dt]
       (vector (if (World/-is-point-in-boundary? (getme missile :position))
                   (assocme missile
                            :position
                            (mapv +
                                  (getme missile :position)
                                  (map (partial * (/ dt 1000))
                                       (getme missile :velocity))))
                   nil)
               world))
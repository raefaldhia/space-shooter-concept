(ns clojure.io.github.raefaldhia.cg_final.bot
    (:require [clojure.io.github.raefaldhia.cg_final.map_entry :refer [assocme getme updateme updateme-in]]
              [clojure.io.github.raefaldhia.cg_final.missile :as Missile]
              [clojure.io.github.raefaldhia.cg_final.spaceship :as Spaceship]
              [clojure.io.github.raefaldhia.cg_final.world :as World]))

(declare -create
         -update-fn
         -render-fn)

(defn -create
      ([{:keys [_rotation _position] :as bot}]
       (-> (Spaceship/-create bot)
           (assocme :color [1.0 0.0 0.0])
           (assocme :velocity [4.0 0.0 0.0])
           (assocme :firing-charge 0)
           (assocme :firing-rate 0.8)
           (updateme :render-fns conj -render-fn)
           (updateme :update-fns conj -update-fn))))

(defn- -update-fn
       [bot world _events dt]
       (let [bot  (cond-> (let [new-position (mapv +
                                                   (getme bot :position)
                                                   (mapv (partial * (/ dt 1000)) (getme bot :velocity)))]
                               (if (World/-is-point-in-boundary? new-position)
                                   (assocme bot :position new-position)
                                   (updateme-in bot [:velocity 0] (partial * (- 1)))))
                          (< (getme bot :firing-charge) 1)  (updateme :firing-charge (partial + (* dt (* (/ 1 1000) (getme bot :firing-rate)))))
                          (>= (getme bot :firing-charge) 1) (assocme :firing-charge 0))
             world (cond-> world
                           (= (getme bot :firing-charge) 0) (World/-put-object (Missile/-create {:position (update (getme bot :position) 1 - (* (getme bot :collision-radius) 2))
                                                                                                 :target (assoc (getme bot :position) 1 (- 20))})))]
            (vector bot world)))

(defn- -render-fn
       [_bot _world gl _glu]
       (.glRotatef gl 90 0.0 0.0 1.0))
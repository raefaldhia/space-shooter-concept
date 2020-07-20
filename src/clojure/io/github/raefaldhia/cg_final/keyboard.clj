(ns clojure.io.github.raefaldhia.cg_final.keyboard)

(declare -create
         -get-state
         -press
         -release)

(defn -create
      []
      (-> (repeat 256 false)
          (vec)))

(defn -get-state
      [keyboard key]
      (get keyboard (int key)))

(defn -press
      [keyboard key]
      (assoc keyboard (int key) true))

(defn -release
      [keyboard key]
      (assoc keyboard (int key) false))

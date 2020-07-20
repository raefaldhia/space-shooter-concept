(ns clojure.io.github.raefaldhia.cg_final.world.object
    (:require [clojure.io.github.raefaldhia.cg_final.map_entry :refer [getme map-entry]]))

(declare -create
         -render
         -render-fn
         -update
         -update-fn)

(defn -create
      ([{:keys [] :as object}]
       (-create nil object))
      ([id {:keys [] :as object}]
       (map-entry (if (nil? id)
                      (gensym ":object")
                      id)
                  (-> object
                      (assoc :render-fns
                             (vector -render-fn))
                      (assoc :update-fns
                             (vector -update-fn))))))

(defn -render
      [object world gl glu]
      (loop [[-render-fn & render-fns] (getme object :render-fns)]
            (when-not (nil? -render-fn)
                      (-render-fn object world gl glu)
                      (recur render-fns))))

(defn- -render-fn
       [_object _world _gl _glu])

(defn -update
      [object world events dt]
      (loop [[object world :as return-value] (vector object world)
             [-update-fn & update-fns] (getme object :update-fns)]
            (if (or (nil? -update-fn)
                    (nil? object))
                return-value
                (recur (-update-fn object world events dt) update-fns))))

(defn- -update-fn
       [object world _events _dt]
       (vector object world))
(ns gforcedev.cub3intime.components.timer (:require [reagent.core :as r]
            [tailwind-hiccup.core :refer [tw]]))

(defonce app-state (r/atom {:current-time 0
                            :timer-phase :stopped}))
(def curr-time (r/cursor app-state [:current-time]))
(def phase (r/cursor app-state [:timer-phase]))

(defn add-missing-decimal [s]
  (if (re-find #"\." s) s (str s ".00")))

(defn pad-to-2 [s]
  (if (= (count s) 2)
    s
    (recur (str "0" s))))

(defn format-time [n]
  (let [[_ raw-secs raw-millis] (->> n (str) (add-missing-decimal) (re-find #"(\d+)\.(\d\d)") (mapv js/parseInt))
        hours (if (>= raw-secs 3600) (-> 3661 (/ 3600) Math/floor) 0)
        mins (if (>= raw-secs 60) (-> (- raw-secs (* 3600 hours)) (/ 60) Math/floor) 0)
        secs (-> raw-secs (- (* hours 3600)) (- (* mins 60)) (str) (pad-to-2))
        millis (-> raw-millis (str) (pad-to-2))
        formatted-hours (if (> hours 0) (str hours ":") "")
        formatted-mins (if (or (> mins 0) (> hours 0)) (str (-> mins (str) ((if (> hours 0) pad-to-2 identity))) ":") "")]
    (str formatted-hours formatted-mins secs "." millis)))

(def timer-state-updaters
  {:stopped (fn [] (swap! curr-time inc))})

(defn timer-component []
  [:button
   (tw [:text-7xl :text-center :p-20]
       {:on-click (timer-state-updaters @phase)})
   (format-time @curr-time)])


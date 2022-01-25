(ns gforcedev.cub3intime.components.timer
  (:require [reagent.core :as r]
            [tailwind-hiccup.core :refer [tw]]
            [goog.events :as events])
  (:import [goog.events EventType]))

(defn pad-to-2 [n]
  (let [s (str n)] (if (= (count s) 2) s (recur (str "0" s)))))

(defn format-time [n]
  (let [floored (js/Math.floor n)
        ms (mod n 1)
        s (mod floored 60)
        m (-> floored (- s) (mod 3600) (/ 60))
        h (-> floored (- s (* m 60)) (/ 3600))]
    (str (if (> h 0) (str h ":") "")
         (if (> h 0)
           (str (pad-to-2 m) ":")
           (if (> m 0) (str m ":") ""))
         (if (some #(> % 0) [h m])
           (pad-to-2 s) s)
         "." (-> ms (* 100) (js/Math.floor) (pad-to-2)))))

(defn display-time [n penalty]
  (case penalty
    "" (format-time n)
    "+2" (-> (+ n 2) (format-time) (str "+"))
    "DNF" "DNF"))

(def time-color-classes
  {:stopped :text-grey-400
   :ready :text-green-400
   :running :text-grey-400
   :stopping :text-red-400})

(defn update-timer-state! [current-phase current-time current-penalty scramble-callback]
  (case @current-phase
    :stopped (do
               (reset! current-time 0)
               (reset! current-penalty "")
               (reset! current-phase :ready))
    :ready (reset! current-phase :running)
    :running (reset! current-phase :stopping)
    :stopping (do
                (reset! current-phase :stopped)
                (scramble-callback))))

(defn inc-time! [last-tick current-phase current-time]
  (let [old-last-tick @last-tick]
    (reset! last-tick (js/Date.now))
    (when (= @current-phase :running)
      (swap! current-time #(+ % (-> (js/Date.now)
                                    (- old-last-tick)
                                    (/ 1000)))))))

(defonce current-phase (r/atom :stopped))
(defonce is-down (r/atom false))
(defonce last-tick (r/atom (js/Date.now)))

(defn timer-component [current-time current-penalty scramble-callback!]
  (defonce keydown-listener
    (events/listen js/window EventType.KEYDOWN
                   #(when (= (.-key %) " ")
                      (when (not @is-down)
                        (reset! is-down true)
                        (update-timer-state! current-phase current-time current-penalty scramble-callback!)))))
  (defonce keyup-listener
    (events/listen js/window EventType.KEYUP
                   #(when (= (.-key %) " ")
                      (reset! is-down false)
                      (update-timer-state! current-phase current-time current-penalty scramble-callback!))))
  (fn [current-time]
    (js/setTimeout #(inc-time! last-tick current-phase current-time) 10)
    [:div
     (tw [:text-7xl :text-center :p-20 (time-color-classes @current-phase)])
     (display-time @current-time @current-penalty)]))


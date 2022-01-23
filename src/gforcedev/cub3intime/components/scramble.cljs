(ns gforcedev.cub3intime.components.scramble
  (:require [tailwind-hiccup.core :refer [tw]]))

(defn scramble-component [scramble-string]
  [:div
   (tw [:text-3xl :text-center :pt-14])
   scramble-string])

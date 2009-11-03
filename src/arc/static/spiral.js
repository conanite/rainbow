(function() {
  function cumulativeOffset(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop || 0;
      valueL += element.offsetLeft || 0;
      if (typeof element.offsetParent == 'undefined' || typeof element.offsetParent == 'unknown') break;
      element = element.offsetParent;
    } while (element);
    return [valueL, valueT];
  }

  function pointer(event) {
    return {
      x: event.pageX || (event.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft)),
      y: event.pageY || (event.clientY + (document.documentElement.scrollTop || document.body.scrollTop))
    };
  }

  function offsets(element, event, f) {
    var offset = cumulativeOffset(element);
    var p = pointer(event || window.event);
    var y = p.y - offset[1];
    var x = p.x - offset[0];
    f(y, x);
  }

  function $(id) {
    return document.getElementById(id);
  }

  function button(name) {
    return $(name + "_button");
  }

  function sc(d, scale, orig) {
    return d / scale + orig;
  }

  function px2xy(event, f) {
    var centre_y = $('main_img').offsetHeight / 2;
    var centre_x = $('main_img').offsetWidth / 2;
    var scale = $('main_img').offsetWidth / zoom();

    offsets($('main_img'), event, function(y, x) {
      y = centre_y - y;
      x = x - centre_x;
      f(sc(y, scale, oy()), sc(x, scale, ox()));
    });
  }

  function installAnimateButton() {
    $('animate').onclick = function() {
      $('spiral_form').action = "/animate";
    };
  }

  function getFPS() {
    var fps = parseFloat($("fps").value);
    if (isNaN(fps) || fps > 100) {
      fps = 100;
    }
    return fps;
  }

  var vars = ["x", "y", "x0", "y0", "zoom", "zoom0", "ox", "oy"];

  for (var i = 0; i < vars.length; i++) {
    var v = vars[i];
    eval("var " + v + " = function(value) {" +
         "  var field = $('" + v + "_field');" +
         "  if (value == undefined) {" +
         "    return parseFloat(field.value);" +
         "  } else {" +
         "    field.value = value; " +
         "  }" +
         "};");
  }

  window.$s = {
    moveTo : function(newx, newy) {
      x(newx);
      y(newy);
      $("spiral_form").submit();
    },

    install : function() {
      var zoomables = [
        ["left", "right", "x"],
        ["up", "down", "y"],
        ["origin_left", "origin_right", "ox"],
        ["origin_up", "origin_down", "oy"]
      ];

      for (var z = 0; z < zoomables.length; z++) {
        var z0 = zoomables[z][0];
        var z1 = zoomables[z][1];
        var zv = zoomables[z][2];
        eval("button('" + z0 + "').onclick = function() {" +
             zv + "(" + zv + "() - (0.05 * zoom()));" +
             "}");
        eval("button('" + z1 + "').onclick = function() {" +
             zv + "(" + zv + "() + (0.05 * zoom()));" +
             "}");
      }

      button("zoom_in").onclick = function() {
        zoom(0.8 * zoom());
      };

      button("zoom_out").onclick = function() {
        zoom(1.2 * zoom());
      };

      button("copy_x").onclick = function() {
        x0(x());
      };

      button("copy_y").onclick = function() {
        y0(y());
      };

      button("copy_zoom").onclick = function() {
        zoom0(zoom());
      };

      installAnimateButton();
    },

    recentering : function() {
      $("main_img").onmousemove = function(event) {
        px2xy(event, function(y, x) {
          $("img_hover").innerHTML = ("click to recenter on " + x + (y < 0 ? "" : "+") + y + "i");
        });
      };

      $("main_img").onclick = function(event) {
        px2xy(event, function(y, x) {
          ox(x);
          oy(y);
        });
      };
    },

    animate : function() {
      var n = 1;

      function step() {
        var fps = getFPS();
        if (fps <= 0) {
          setInterval(step, 50);
          return;
        }

        var loaded = 0;
        var img = $("anim_" + n);
        img.style.display = 'none';

        n++;

        var ld = 1;
        img = $("anim_" + ld);
        while (img) {
          if (img.complete) {
            loaded++;
          }
          ld++;
          img = $("anim_" + ld);
        }

        img = $("anim_" + n);
        while (img && (!img.complete)) {
          img.style.display = 'none';
          n++;
          img = $("anim_" + n);
        }

        if (!img) {
          n = 1;
          img = $("anim_" + n);
        }

        fps = getFPS();
        $("animinfo").innerHTML = "frame " + n + ",<br/>" + img.alt + "<br/>(loaded " + loaded + " of " + ld + ")<br/>" + fps + "fps";
        if (img.complete) {
          img.style.display = '';
        }

        setTimeout(step, 1000/fps);
      }

      step();
    }

  };
})();

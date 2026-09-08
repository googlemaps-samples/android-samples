const RUN_ID = "__RUN_ID__";
    const RAW_RESULTS = __RAW_RESULTS__;
    let currentFilter = "all";
    let operatorData = {};
    let currentCardIndex = 1;
    let allVideosVisible = false;
    let allComparisonsVisible = false;

    function init() {
      const saved = localStorage.getItem("gmp_qa_notes_" + RUN_ID);
      if (saved) {
        try {
          operatorData = JSON.parse(saved);
        } catch (e) {}
      }
      RAW_RESULTS.forEach(r => {
        const idx = r.index;
        const noteEl = document.getElementById("notes-" + idx);
        const overEl = document.getElementById("override-" + idx);
        if (!operatorData[idx] && (r.operator_notes || r.operator_flagged)) {
          operatorData[idx] = {
            notes: r.operator_notes || "",
            flagged: Boolean(r.operator_flagged || (r.operator_notes && r.operator_notes.trim().length > 0))
          };
        }
        if (operatorData[idx]) {
          if (noteEl && operatorData[idx].notes) noteEl.value = operatorData[idx].notes;
          const hasNotes = Boolean(operatorData[idx].notes && operatorData[idx].notes.trim().length > 0);
          if (hasNotes) {
            operatorData[idx].flagged = true;
          }
          if (overEl && operatorData[idx].flagged) overEl.checked = true;
        }
      });
      updateNotesCount();

      document.addEventListener("keydown", (e) => {
        if (document.activeElement && (document.activeElement.tagName === "INPUT" || document.activeElement.tagName === "TEXTAREA")) {
          return;
        }
        if (e.key === "j") {
          navigateCard(1);
        } else if (e.key === "k") {
          navigateCard(-1);
        } else if (e.key === "v") {
          toggleAllMedia();
        } else if (e.key === "c") {
          toggleAllComparisons();
        } else if (e.key === "/") {
          e.preventDefault();
          const s = document.getElementById("searchInput");
          if (s) s.focus();
        }
      });
    }

    function switchRun(selectedRun) {
      if (!selectedRun) return;
      window.location.href = "/?run=" + encodeURIComponent(selectedRun);
    }

    function navigateCard(delta) {
      const cards = Array.from(document.querySelectorAll(".sample-card")).filter(c => c.style.display !== "none");
      if (cards.length === 0) return;
      let curIdx = cards.findIndex(c => parseInt(c.dataset.index) === currentCardIndex);
      if (curIdx === -1) curIdx = 0;
      let nextIdx = curIdx + delta;
      if (nextIdx < 0) nextIdx = 0;
      if (nextIdx >= cards.length) nextIdx = cards.length - 1;
      const target = cards[nextIdx];
      currentCardIndex = parseInt(target.dataset.index);
      document.querySelectorAll(".sample-card").forEach(c => c.classList.remove("focused"));
      target.classList.add("focused");
      target.scrollIntoView({ behavior: "smooth", block: "center" });
    }

    function saveLocal() {
      localStorage.setItem("gmp_qa_notes_" + RUN_ID, JSON.stringify(operatorData));
      updateNotesCount();
    }

    function handleNoteChange(idx) {
      const text = document.getElementById("notes-" + idx).value;
      if (!operatorData[idx]) operatorData[idx] = {};
      operatorData[idx].notes = text;
      // If there is feedback, then there is an issue: auto-flag
      const hasFeedback = Boolean(text && text.trim().length > 0);
      operatorData[idx].flagged = hasFeedback;
      const overEl = document.getElementById("override-" + idx);
      if (overEl) {
        overEl.checked = hasFeedback;
      }
      saveLocal();
      const statusEl = document.getElementById("saved-status-" + idx);
      if (statusEl) {
        statusEl.innerText = hasFeedback ? "🚩 Flagged • Saved " + new Date().toLocaleTimeString() : "Saved " + new Date().toLocaleTimeString();
      }
    }

    function handleOverrideChange(idx) {
      const checked = document.getElementById("override-" + idx).checked;
      if (!operatorData[idx]) operatorData[idx] = {};
      operatorData[idx].flagged = checked;
      saveLocal();
      const statusEl = document.getElementById("saved-status-" + idx);
      if (statusEl) {
        statusEl.innerText = checked ? "🚩 Flagged • Saved " + new Date().toLocaleTimeString() : "Saved " + new Date().toLocaleTimeString();
      }
    }

    function insertDirective(idx, text) {
      const el = document.getElementById("notes-" + idx);
      el.value = el.value ? el.value + "\n" + text : text;
      el.focus();
      handleNoteChange(idx);
    }

    function updateNotesCount() {
      let count = 0;
      Object.keys(operatorData).forEach(k => {
        if (operatorData[k].notes && operatorData[k].notes.trim()) count++;
      });
      const el = document.getElementById("notes-count");
      if (el) el.innerText = count;
    }

    function showToast(msg) {
      const t = document.getElementById("toast");
      t.innerText = msg;
      t.classList.add("show");
      setTimeout(() => t.classList.remove("show"), 2500);
    }

    function switchMediaTab(idx, mode) {
      const stillGrid = document.getElementById("stills-grid-" + idx);
      const videoGrid = document.getElementById("videos-grid-" + idx);
      const compareGrid = document.getElementById("compare-grid-" + idx);
      const stillTab = document.getElementById("tab-still-" + idx);
      const videoTab = document.getElementById("tab-video-" + idx);
      const compareTab = document.getElementById("tab-compare-" + idx);

      if (stillGrid) stillGrid.style.display = "none";
      if (videoGrid) videoGrid.style.display = "none";
      if (compareGrid) compareGrid.style.display = "none";

      if (stillTab) stillTab.classList.remove("active");
      if (videoTab) videoTab.classList.remove("active");
      if (compareTab) compareTab.classList.remove("active");

      if (mode === "video" && videoGrid) {
        videoGrid.style.display = "grid";
        if (videoTab) videoTab.classList.add("active");
        videoGrid.querySelectorAll("video").forEach(v => {
          v.play().catch(() => {});
        });
      } else if (mode === "compare" && compareGrid) {
        compareGrid.style.display = "grid";
        if (compareTab) compareTab.classList.add("active");
      } else if (stillGrid) {
        stillGrid.style.display = "grid";
        if (stillTab) stillTab.classList.add("active");
      }
    }

    function toggleAllMedia() {
      allVideosVisible = !allVideosVisible;
      allComparisonsVisible = false;
      const mode = allVideosVisible ? "video" : "still";
      RAW_RESULTS.forEach(r => {
        if (r.java_video || r.kotlin_video) {
          switchMediaTab(r.index, mode);
        }
      });
      const btn = document.getElementById("btn-toggle-all-media");
      if (btn) {
        btn.innerText = allVideosVisible ? "🖼️ Show All Stills" : "🎬 Show All Videos";
      }
      const cmpBtn = document.getElementById("btn-toggle-compare");
      if (cmpBtn) cmpBtn.innerText = "🔄 Compare with Prior Run";
      showToast(allVideosVisible ? "Displaying recorded motion videos (25%)" : "Displaying still screenshots (50%)");
    }

    function toggleAllComparisons() {
      allComparisonsVisible = !allComparisonsVisible;
      allVideosVisible = false;
      const mode = allComparisonsVisible ? "compare" : "still";
      RAW_RESULTS.forEach(r => {
        switchMediaTab(r.index, mode);
      });
      const btn = document.getElementById("btn-toggle-compare");
      if (btn) {
        btn.innerText = allComparisonsVisible ? "🖼️ Show All Stills" : "🔄 Compare with Prior Run";
      }
      const vidBtn = document.getElementById("btn-toggle-all-media");
      if (vidBtn) vidBtn.innerText = "🎬 Show All Videos";
      showToast(allComparisonsVisible ? "Displaying Before vs After comparisons" : "Displaying still screenshots");
    }

    function openLightbox(src, isVideo = false) {
      const lb = document.getElementById("lightbox");
      const img = document.getElementById("lightbox-img");
      const vid = document.getElementById("lightbox-video");
      if (isVideo) {
        img.style.display = "none";
        vid.src = src;
        vid.style.display = "block";
        vid.load();
        vid.play().catch(() => {});
      } else {
        if (vid) {
          vid.pause();
          vid.style.display = "none";
        }
        img.src = src;
        img.style.display = "block";
      }
      lb.style.display = "flex";
    }

    function closeLightbox(event) {
      if (event && event.target && (event.target.id === "lightbox-video" || event.target.tagName === "VIDEO")) {
        return;
      }
      const lb = document.getElementById("lightbox");
      const vid = document.getElementById("lightbox-video");
      if (vid) {
        vid.pause();
        vid.src = "";
      }
      lb.style.display = "none";
    }

    function setFilter(filter) {
      currentFilter = filter;
      document.querySelectorAll(".stats-chips .chip").forEach(c => c.classList.remove("active"));
      if (window.event && window.event.target) window.event.target.classList.add("active");
      applyFilterAndSearch();
    }

    function handleSearch() {
      applyFilterAndSearch();
    }

    function applyFilterAndSearch() {
      const query = document.getElementById("searchInput").value.toLowerCase().trim();
      document.querySelectorAll(".sample-card").forEach(card => {
        const idx = card.dataset.index;
        const status = card.dataset.status;
        const searchData = card.dataset.search;
        const hasNotes = operatorData[idx] && operatorData[idx].notes && operatorData[idx].notes.trim().length > 0;
        const hasPrior = card.dataset.hasPrior === "true";

        let matchesFilter = false;
        if (currentFilter === "all") matchesFilter = true;
        else if (currentFilter === "needs_work" && status === "needs_work") matchesFilter = true;
        else if (currentFilter === "passing" && status === "passing") matchesFilter = true;
        else if (currentFilter === "with_video" && card.dataset.hasVideo === "true") matchesFilter = true;
        else if (currentFilter === "with_prior" && hasPrior) matchesFilter = true;
        else if (currentFilter === "with_notes" && hasNotes) matchesFilter = true;

        let matchesSearch = !query || searchData.includes(query) || (operatorData[idx] && operatorData[idx].notes && operatorData[idx].notes.toLowerCase().includes(query));

        card.style.display = (matchesFilter && matchesSearch) ? "block" : "none";
      });
    }

    function getCombinedData() {
      return RAW_RESULTS.map(r => {
        const idx = r.index;
        const userEntry = operatorData[idx] || {};
        const hasNotes = Boolean(userEntry.notes && userEntry.notes.trim().length > 0);
        return {
          ...r,
          operator_notes: userEntry.notes || "",
          operator_flagged: Boolean(hasNotes || userEntry.flagged),
        };
      });
    }

    function exportCombinedJson() {
      const data = {
        run_id: RUN_ID,
        export_date: new Date().toISOString(),
        total_samples: RAW_RESULTS.length,
        samples: getCombinedData()
      };
      const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "combined_qa_report_" + RUN_ID + ".json";
      a.click();
      URL.revokeObjectURL(url);
      showToast("Exported combined JSON!");
    }

    function exportCombinedMarkdown() {
      let md = "# 📊 Combined QA Evaluation & Operator Feedback Report\n";
      md += "> Run: `" + RUN_ID + "` | Date: " + new Date().toLocaleString() + "\n\n";
      const combined = getCombinedData();
      combined.forEach(r => {
        md += "### #" + String(r.index).padStart(2, "0") + " " + r.title + " (" + r.category + ")\n";
        md += "- **Status**: `" + r.status + "`\n";
        md += "- **Purpose**: " + (r.purpose || "N/A") + "\n";
        md += "- **Success Criteria**: " + (r.successCriteria || "N/A") + "\n";
        if (r.prior_directive_info) {
          md += "- **🎯 Prior Directive**: " + r.prior_directive_info.prior_directive + "\n";
          md += "- **🛠️ Resolution**: " + r.prior_directive_info.action_taken + "\n";
        }
        md += "- **Agent Finding**: " + (r.notes ? r.notes.replace(/\n/g, " ") : "Verified") + "\n";
        if (r.java_video || r.kotlin_video) {
          let vids = [];
          if (r.java_video) vids.push("[Java Video](" + r.java_video + ")");
          if (r.kotlin_video) vids.push("[Kotlin Video](" + r.kotlin_video + ")");
          md += "- **🎬 Video Replay (25%)**: " + vids.join(" | ") + "\n";
        }
        if (r.operator_notes) {
          md += "- **✍️ Operator Notes (dkhawk)**: " + r.operator_notes + "\n";
        }
        md += "\n---\n\n";
      });
      const blob = new Blob([md], { type: "text/markdown" });
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = "combined_qa_report_" + RUN_ID + ".md";
      a.click();
      URL.revokeObjectURL(url);
      showToast("Exported combined Markdown!");
    }

    function copyLlmPrompt() {
      const combined = getCombinedData();
      const withFeedback = combined.filter(r => (r.operator_notes && r.operator_notes.trim()) || r.status === "NEEDS_WORK");
      if (withFeedback.length === 0) {
        showToast("No operator notes or defects to copy!");
        return;
      }
      let prompt = "Here is my review feedback and probing directives for the GMP Android Catalog run `" + RUN_ID + "`:\n\n";
      withFeedback.forEach(r => {
        prompt += "### " + r.title + " (`" + (r.kotlinActivity || r.id).split(".").pop() + "`)\n";
        prompt += "- **Status**: " + r.status + "\n";
        if (r.operator_notes) {
          prompt += "- **✍️ My Notes / Interaction Probing Directives**: " + r.operator_notes + "\n";
        }
        if (r.status === "NEEDS_WORK") {
          prompt += "- **Defect Finding**: " + r.notes.split("\n")[0] + "\n";
        }
        prompt += "\n";
      });
      prompt += "Please update the test suite scripts (SAMPLE_ACTIONS, settle times, or sample code) to address these directives and re-verify.";

      navigator.clipboard.writeText(prompt).then(() => {
        showToast("📋 Copied LLM Feedback Prompt to clipboard!");
      }).catch(() => {
        showToast("Failed to copy automatically. Use export button.");
      });
    }

    function saveToServer() {
      const data = {
        run_id: RUN_ID,
        export_date: new Date().toISOString(),
        samples: getCombinedData()
      };
      fetch("/api/save_notes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
      }).then(res => {
        if (res.ok) {
          showToast("💾 Saved notes directly to run directory on disk!");
        } else {
          showToast("Local server not responding; using localStorage.");
        }
      }).catch(() => {
        showToast("Standalone mode: using localStorage. Use Export buttons.");
      });
    }

    window.addEventListener("DOMContentLoaded", init);
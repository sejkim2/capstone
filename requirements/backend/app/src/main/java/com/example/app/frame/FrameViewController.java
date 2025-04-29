package com.example.app.frame;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FrameViewController {

    private static final String FRAME_DIR = "/frames";
    private static final int PAGE_SIZE = 20;

    @GetMapping("/frames/view")
    public String viewFrames(@RequestParam(defaultValue = "1") int page, Model model) {
        File folder = new File(FRAME_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            model.addAttribute("images", List.of());
            model.addAttribute("totalPages", 1);
            model.addAttribute("currentPage", page);
            return "frame_list";
        }

        List<String> allImages = Arrays.stream(folder.listFiles((dir, name) -> name.endsWith(".jpg")))
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) allImages.size() / PAGE_SIZE);
        int fromIndex = Math.min((page - 1) * PAGE_SIZE, allImages.size());
        int toIndex = Math.min(page * PAGE_SIZE, allImages.size());

        List<String> pagedImages = allImages.subList(fromIndex, toIndex);

        model.addAttribute("images", pagedImages);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        return "frame_list";
    }
}

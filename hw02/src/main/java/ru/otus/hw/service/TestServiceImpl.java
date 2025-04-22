package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final String errorMsg = "Your choice out of available range! Please try again.";

    private final IOService ioService;

    private final QuestionDao questionDao;


    private void printAnswer(Answer answer, AtomicInteger answerIndex) {
        ioService.printFormattedLine("\t%d) %s", answerIndex.incrementAndGet(), answer.text());
    }

    private void printQuestion(Question question, AtomicInteger questionIndex) {
        ioService.printFormattedLine("%d. %s", questionIndex.incrementAndGet(), question.text());
        AtomicInteger answerIndex = new AtomicInteger();
        question.answers().forEach(answer -> printAnswer(answer, answerIndex));
    }

    private Integer questionCorrectAnswerNumber(Question question) {
        var correctAnswer = question.answers().stream()
                .filter(Answer::isCorrect)
                .findFirst();
        return question.answers().indexOf(correctAnswer.get()) + 1;
    }


    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        AtomicInteger answerIndex = new AtomicInteger();
        for (var question : questions) {
            printQuestion(question, answerIndex);
            var choice = ioService.readIntForRange(1, question.answers().size(), errorMsg);
            var isAnswerValid = questionCorrectAnswerNumber(question).equals(choice);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
